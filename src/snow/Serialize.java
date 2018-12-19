package snow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import snow.session.Preferences;

public class Serialize {
	
	public static final String PREFERENCES_PATH = "./preferences.s"; // TODO Find a valid path
	
	public synchronized static void savePreferences(Preferences prefs) {
		try {
			storeSerializableClass(prefs, new File(PREFERENCES_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static Preferences loadPreferences() {
		try {
			return (Preferences) loadSerializedFile(new File(PREFERENCES_PATH));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Object loadSerializedFile(File f) throws IOException, ClassNotFoundException {
		if (!f.exists())
			return null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}
	
	public static final void storeSerializableClass(Serializable o, File f) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}

}
