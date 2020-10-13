package com.hadden;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

import org.retro.common.ImageHandler;
import org.retro.common.ImageHandlerFactory;
import org.retro.common.ImageType;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualFile;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.ReadOnlyException;
import de.waldheinz.fs.fat.FatFile;
import de.waldheinz.fs.fat.FatFileSystem;
import de.waldheinz.fs.fat.FatLfnDirectoryEntry;
import de.waldheinz.fs.fat.FatType;
import de.waldheinz.fs.fat.SuperFloppyFormatter;
import de.waldheinz.fs.util.FileDisk;
import de.waldheinz.fs.util.RamDisk;

public class TestWrite
{
	public void createDiskImage(File outFile) throws IOException
	{
		final FileDisk fd = FileDisk.create(outFile, 8l * 1024 * 1024 * 1024);
		final FatFileSystem fs = SuperFloppyFormatter.get(fd).setFatType(FatType.FAT32).setVolumeLabel("huhu").format();
		try
		{
			//this.copyRec(this.imageRoot, fs.getRoot());
		}
		finally
		{
			fs.close();
			fd.close();
		}
	}

	public static void main(String[] args)
	{
		String filename = "c:\\devtools\\FAT\\f.img";

		
		File diskImageFile = new File(filename);
		
		//FileDisk dev = FileDisk.create(new File(filename), 720 * 1024 );
		//FatFileSystem fs = SuperFloppyFormatter.get(dev).setFatType(FatType.FAT12).format();
		
		try
		{
			String dosFileName = "H.OUT";
			
			FileDisk dev = new FileDisk(diskImageFile,false);
			FatFileSystem fs = FatFileSystem.read(dev, false);
			FatLfnDirectoryEntry file = fs.getRoot().addFile(dosFileName);
			FatFile ff = file.getFile();
			ff.write(0,  ByteBuffer.wrap(("CONTENTS OF " + dosFileName).getBytes("utf-8")));
			ff.flush();
			fs.flush();
			dev.flush();
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		ImageType type = ImageType.getTypeFromFileSuffix(filename.substring(filename.indexOf(".") + 1));
		System.out.println("Attempt to load image of type: " + type.name());
		if (type != ImageType.unknown)
		{
			ImageHandler handler = ImageHandlerFactory.get(type);
			try
			{
				VirtualDisk disk = handler.loadImage(diskImageFile);
				VirtualDirectory root = disk.getRootContents();

				List<VirtualFile> contents = root.getContents();
				for (VirtualFile vf : contents)
				{
					System.out.println(vf.getName());
					if(vf.getName().endsWith(".OUT"))
					{
						System.out.println("\tCONTENT:" + new String(vf.getContent().array(),"utf-8"));
					}
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
