package com.snow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serialize {

	public synchronized static void saveInitialData(InitialData data) throws IOException {
		storeSerializableClass(data, new File(InitialData.PATH));
	}

	public synchronized static InitialData loadInitialData() {
		try {
			InitialData data = (InitialData) loadSerializedFile(new File(InitialData.PATH));
			
			if (data != null)
				data.handleData();
			
			return data;
		} catch (Throwable e) {
			System.err.println("Error loading data...");
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
