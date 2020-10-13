package com.hadden;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.retro.common.ImageHandler;
import org.retro.common.ImageHandlerFactory;
import org.retro.common.ImageType;
import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualFile;

import de.waldheinz.fs.BlockDevice;
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

		ImageType type = ImageType.getTypeFromFileSuffix(filename.substring(filename.indexOf(".") + 1));
		System.out.println("Attempt to load image of type: " + type.name());
		if (type != ImageType.unknown)
		{
			ImageHandler handler = ImageHandlerFactory.get(type);
			try
			{
				FileDisk dev = new FileDisk(new File(filename),false);
				//FileDisk dev = FileDisk.create(new File(filename), 720 * 1024 );
				
				//FatFileSystem fs = SuperFloppyFormatter.get(dev).setFatType(FatType.FAT12).format();
				FatFileSystem fs = FatFileSystem.read(dev, false);
				FatLfnDirectoryEntry file = fs.getRoot().addFile("E.OUT");
				FatFile ff = file.getFile();
				ff.write(0,  ByteBuffer.wrap("YAYAYAYAYAYAYAYAY!".getBytes("utf-8")));
				ff.flush();
				fs.flush();
				dev.flush();
				
				/*
				VirtualDisk disk = handler.loadImage(new File(filename));
				VirtualDirectory root = disk.getRootContents();

				List<VirtualFile> contents = root.getContents();
				for (VirtualFile file : contents)
				{
					System.out.println(file.getName());
				}

				VirtualFile newFile = root.addFile("A.OUT");
				newFile.setContent("HELLO FMX!".getBytes("utf-8"));
				newFile.setParent(root);
				*/
				//handler.writeImage(dev, new File(filename));
				
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
