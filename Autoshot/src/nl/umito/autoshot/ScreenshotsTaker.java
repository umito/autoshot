package nl.umito.autoshot;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
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
import android.graphics.Rect;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.InstrumentationTestRunner;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ViewFlipper;

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
			resetSavedAppPreferences(getInstrumentation().getTargetContext());
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
	
	/**
	 * Example of using Robotium to move through keychord app and take screenshots
	 * Won't compile ofcourse! 
	 * @author Peter
	 *
	 */
//	public void takeScreenshots() throws Throwable {
//		resetScreenshotIndex();
//	
//		solo.sleep(1000);
//		if (isLite)
//		{
//			solo.clickOnText(solo.getString(R.string.continue_button));
//			solo.sleep(500);
//		}
//		takeScreenshot("dashboard");
//
//		// remove help messages
//		CurrentData.setStaffHelpMessageShown(true);
//		CurrentData.setReverseInfoMessageShown(true);
//		CurrentData.setNormalInfoMessageShown(true);
//		CurrentData.setScaleHelpMessageShown(true);
//
//		int[] dashboardButtons = { R.id.dashboard_chords_button,
//				R.id.dashboard_scales_button,
//				R.id.dashboard_staff_dictionary_button,
//				R.id.dashboard_reverse_chords_button,
//				R.id.dashboard_reverse_scales_button,
//				R.id.dashboard_minipiano_button };
//		String[] buttonNames = { "chords", "scales", "staff", "reverse_chords",
//				"reverse_scales", "minipiano" };
//		boolean[] hasSlidingDrawer = { true, true, true, false, false, false };
//		for (int i = 0; i < dashboardButtons.length; i++) {
//
//			int id = dashboardButtons[i];
//			if (isLite) {
//				if (id == R.id.dashboard_staff_dictionary_button
//						|| id == R.id.dashboard_reverse_chords_button) {
//					continue;
//				}
//			}
//			View button = solo.getView(id);
//			solo.clickOnView(button);
//			solo.sleep(1000);
//
//			String name = buttonNames[i];
//			takeScreenshot(name + "_default");
//			solo.sleep(1000);
//			if (hasSlidingDrawer[i]) {
//				View handle = solo.getView(R.id.ActionBar_Handle_Dots);
//				View bottomItem = solo.getView(R.id.Statusbar);
//				Log.d("sliding", handle.getX() + "," + handle.getX() + ","
//						+ handle.getY() + "," + bottomItem.getY());
//				solo.drag(handle.getX(), handle.getX(), handle.getY(),
//						bottomItem.getY(), 10);
//				solo.sleep(1000);
//				takeScreenshot(name + "_default_slidingdrawer");
//				solo.sleep(100);
//
//				switch (id) {
//				case R.id.dashboard_chords_button: {
//					solo.clickOnText("7");
//					solo.sleep(1000);
//					takeScreenshot(name + "_chord_selected");
//					break;
//				}
//				case R.id.dashboard_scales_button: {
//					solo.clickOnText("^Major");
//					solo.sleep(1000);
//					takeScreenshot(name + "_scale_selected");
//					break;
//				}
//				case R.id.dashboard_staff_dictionary_button: {
//					final ViewFlipper flipper = (ViewFlipper) solo
//							.getView(R.id.drawer_viewflipper);
//					runTestOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							flipper.setDisplayedChild(1);
//
//						}
//					});
//
//					solo.clickOnText("-7");
//					solo.sleep(1000);
//					takeScreenshot(name + "_chord_selected");
//
//					solo.drag(handle.getX(), handle.getX(), handle.getY(),
//							bottomItem.getY(), 10);
//					runTestOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							flipper.setDisplayedChild(2);
//
//						}
//					});
//					solo.clickOnText("Blues");
//					solo.sleep(1000);
//					takeScreenshot(name + "_scale_selected");
//					break;
//				}
//				}
//			}
//			switch (id) {
//			case R.id.dashboard_reverse_chords_button: {
//				final NoteSelectionPiano piano = (NoteSelectionPiano) solo
//						.getView(R.id.NoteSelectionPiano);
//				Note[] chord = { Note.parse("E4"), Note.parse("G#4"),
//						Note.parse("B4"), Note.parse("D5") };
//				HorizontalScrollView scrollView = (HorizontalScrollView) solo
//						.getView(R.id.ScrollView);
//				int scrollX = scrollView.getScrollX();
//				for (Note note : chord) {//
//
//					Rect touchRect = piano.getKeyboard().getKey(note)
//							.getTouchRegion().getBounds();
//					int cx = touchRect.centerX();
//					int cy = touchRect.centerY();
//					int touchX = cx - scrollX;
//					int touchY = (int) (scrollView.getY() + cy);
//					solo.clickOnScreen(touchX, touchY);
//					solo.sleep(25);
//
//				}
//				solo.sleep(1000);
//				takeScreenshot(name + "_chord_selected");
//				break;
//			}
//			case R.id.dashboard_reverse_scales_button: {
//				final NoteSelectionPiano piano = (NoteSelectionPiano) solo
//						.getView(R.id.NoteSelectionPiano);
//
//				String[] noteNames = { "C4", "D4", "E4", "F#4", "G4", "A4",
//						"B4" };
//				ArrayList<Note> notes = new ArrayList<Note>();
//				for (String noteName : noteNames) {
//					notes.add(Note.parse(noteName));
//				}
//				HorizontalScrollView scrollView = (HorizontalScrollView) solo
//						.getView(R.id.ScrollView);
//				int scrollX = scrollView.getScrollX();
//				for (Note note : notes) {//
//
//					Rect touchRect = piano.getKeyboard().getKey(note)
//							.getTouchRegion().getBounds();
//					int cx = touchRect.centerX();
//					int cy = touchRect.centerY();
//					int touchX = cx - scrollX;
//					int touchY = (int) (scrollView.getY() + cy);
//					solo.clickOnScreen(touchX, touchY);
//					solo.sleep(25);
//
//				}
//				solo.sleep(1000);
//				takeScreenshot(name + "_scale_selected");
//				break;
//			}
//			}
//
//			solo.goBack();
//
//		}		
//	}
	
}
