package nl.umito.autoshot;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;
import com.robotium.solo.Solo.Config.ScreenshotFileType;

import nl.umito.autoshot_target.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.InstrumentationTestRunner;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * We use a ActivityInstrumentationTestCase2 here with the target activity as
 * generic type. Note that this does not mean that the MainActivity is the only
 * activity we take screenshots off, It only serves as the starting point
 * 
 * @author Peter de Kraker - Umito
 *
 */
public class ScreenshotsTaker extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private String baseOutputPath;
	private Solo solo;
	private String locale;
	private Config config;
	private int screenshotIndex;

	public ScreenshotsTaker() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {

		baseOutputPath = Environment.getExternalStorageDirectory()
				+ "/Autoshot/"; // We save screenshots to SD
		baseOutputPath += "target_app/"; // The app name as a subdir
		
		// PNG screenshots to correct outdir
		config = new Config();
		config.screenshotFileType = ScreenshotFileType.PNG;
		config.screenshotSavePath = baseOutputPath;

		// We remove all existing screenshots (!)
		FileUtils.deleteDirectory(new File(config.screenshotSavePath));
		
		solo = new Solo(getInstrumentation(), config);

		resetSavedAppPreferences(getInstrumentation().getTargetContext());

		getActivity();

		super.setUp();
	}

	// We want to reset the app to it's original state every time we do a
	// different run/locale
	private void resetSavedAppPreferences(Context targetAppContext) {
		PreferenceManager.getDefaultSharedPreferences(targetAppContext).edit()
				.clear().commit();
	}

	/**
	 * 
	 * @param a
	 * @param locale
	 *            "en" or "en-US" etc.
	 * @throws IOException
	 */
	private void changeActivityLocale(final Activity a, String locale)
			throws IOException {
		Resources res = a.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		if (locale.contains("-")) {
			String[] splitted = locale.split("-");
			conf.locale = new Locale(splitted[0], splitted[1]);
		} else {
			conf.locale = new Locale(locale);
		}
		res.updateConfiguration(conf, dm);
		a.getResources().updateConfiguration(conf, dm);

		this.locale = locale;

	}

	private void updateCurrentScreenshotPath() {
		int widthDP = getInstrumentation().getTargetContext().getResources()
				.getConfiguration().screenWidthDp;
		config.screenshotSavePath = baseOutputPath + ("w" + widthDP + "dp")
				+ "/" + locale + "/";
		new File(config.screenshotSavePath).mkdirs();
	}
	
	private void restartActivity()
	{
		Intent intent = new Intent(getActivity(), getActivity().getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK); //Start new task, and auto finish all previous activities
		getActivity().startActivity(intent);
		solo.sleep(1000);
	}
	
	private void takeScreenshot(String name) {
		solo.takeScreenshot(locale + "_" + ++screenshotIndex + "_" + name);
		solo.sleep(100);
	}
	
	public void resetScreenshotIndex() {
		this.screenshotIndex = 0;
	}	
	
	public void testTakeLocalizedScreenshots() throws Throwable {
		String[] locales = { "EN", "NL" };
		for (String locale : locales) {
			//Log progress
			Log.d("Screenshots", locale);			
			for (int i = 0; i < locales.length; i++) {
				if (locales[i].equals(locale)) {
					Log.d("Screenshots", (i + 1) + "/" + locales.length);
					break;
				}
			}

			//Restart main activity with the proper locale and update the screenshot path
			changeActivityLocale(getActivity(), locale);
			restartActivity();
			updateCurrentScreenshotPath();
			
			//Take the screenshots
			takeScreenshots();
		}
	}
	
	//All screenshot logic goes here.
	public void takeScreenshots() throws Throwable {
		resetScreenshotIndex();		
		solo.sleep(1000);		
		takeScreenshot("dashboard");
	}
}
