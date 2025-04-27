package com.example.wildqueue.utils;

import com.example.wildqueue.models.User;
import java.io.*;

public class Serialize {

	public static void serialize(User user, String filename) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(user);
			System.out.println("User serialized successfully to " + filename);
		} catch (IOException e) {
			System.err.println("Error serializing user: " + e.getMessage());
		}
	}

	public static User deserialize(String filename) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			User user = (User) ois.readObject();
			System.out.println("User deserialized successfully from " + filename);
			return user;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error deserializing user: " + e.getMessage());
		}
		return null;
	}
}
