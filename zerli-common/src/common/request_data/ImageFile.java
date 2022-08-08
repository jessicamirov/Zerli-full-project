package common.request_data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import javafx.scene.image.Image;

/**
 * Images in SQL can be stored as files or strings. When sending image from
 * server to client, need to convert image to "String" as part of JSONObject.
 * When received by Client, need to convert String to Image via Buffered stream.
 * 
 * @author Katya
 */
public class ImageFile {
	public static String asEncodedString(String filePath) {
		File f = new File(filePath);
		try (BufferedInputStream fileInputStreamReader = new BufferedInputStream(new FileInputStream(f))) {
			byte[] bytes = new byte[(int) f.length()];
			fileInputStreamReader.read(bytes);
			return Base64.getEncoder().encodeToString(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Image asImage(String encodedString) {
		if (encodedString == null || encodedString.isEmpty()) {
			/* TODO: add "no image" picture. */
			return new Image("");
		}
		byte[] decodedString = Base64.getDecoder().decode(encodedString);
		return new Image(new ByteArrayInputStream(decodedString));
	}
}
