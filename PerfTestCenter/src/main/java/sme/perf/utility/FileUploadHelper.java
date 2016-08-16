package sme.perf.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadHelper {
	
	public static void upload(MultipartFile file, File filePath)
			throws IOException, FileNotFoundException {
		if(!filePath.exists()){
			filePath.mkdirs();
		}
		
		byte[] bytes = file.getBytes();
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filePath + File.separator + file.getOriginalFilename()));
		stream.write(bytes);
		stream.close();
	}
}
