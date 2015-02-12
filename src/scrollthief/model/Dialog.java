package scrollthief.model;

/*
 * This is for general dialog that is level independent
 */
public class Dialog {
	
	public static String DEFAULT_DIALOG = "There will be a financial oppotunity in your future. Favorite numbers: 2, 15, 29";

	public Dialog() {}
	
	public static String getDialog(LevelState level) {
		return DEFAULT_DIALOG;
	}
}
