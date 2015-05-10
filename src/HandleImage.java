import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class HandleImage {
	
	private int in_files_number;
	private int out_files_number;
	private final String src_img_path = "image/lena512.bmp";
	private final String gen_img_path = "decode/result.bmp";
	private final int BMP_HEAD = 54;
	private ArrayList<FileOutputStream> outs;
	private ArrayList<FileInputStream> ins;
	private ArrayList<File> files;
	private int private_key = 0;
	
	public HandleImage() {
		// TODO Auto-generated constructor stub
	}
	
	// 生成密钥，每次加密前需要做
	public void generateKey(){
		private_key = (int)(Math.random()*15+1);
	}
	
	// 获取密钥
	public int getKey(){
		return private_key;
	}
	
	// 加密一位
	public int encodeByte(int e){
		int left = e >> 4;  // 获得左侧
		int right = e & 15;  // 获得右侧
		int result = (right << 4) + (left ^ private_key);
		return result;
	}
	
	// 解密一位
	public int decodeByte(int d){
		int right = d & 15;
		int left = d >> 4;
		int result = ((right ^ private_key) << 4) + left;
		return result;
	}
	
	public void initInNumber(int n){
		this.in_files_number = n;
	}
	
	public void initOutNumber(int n){
		this.out_files_number = n;
	}
	
	// 创建文件
	public ArrayList<File> createFiles(int n){
		files = new ArrayList<File>();
		String path_name;
		for (int i = 0; i < n; i++) {
			path_name = "generate/"+i+".bmp";
			File file = new File(path_name);
			files.add(file);
		}
		return files;
	}
	
	// 清除文件夹下所有文件
	public boolean clearFiles(String dir_name){
		try {
			File file = new File(dir_name);
			File[] mult_files = file.listFiles();
			for (int i = 0; i < mult_files.length; i++) {
				mult_files[i].delete();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	// 创建字节输出流
	public ArrayList<FileOutputStream> createOutPutStream(){
		outs = new ArrayList<FileOutputStream>();
		if (!clearFiles("generate")) {
			return null;
		}
		files = createFiles(out_files_number);
		// 文件没有创建成功
		if (files==null) {
			return null;
		}
		try {
			for (int i = 0; i < out_files_number; i++) {
				FileOutputStream out = new FileOutputStream((File)files.get(i));
				outs.add(out);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return outs;
	}
	
	// 创建字节输入流
	public ArrayList<FileInputStream> createInPutStream(){
		ins = new ArrayList<FileInputStream>();
		if (!clearFiles("decode")) {
			return null;
		}
		files = createFiles(in_files_number);
		// 文件没有创建成功
		if (files==null){
			return null;
		}
		try {
			for (int i = 0; i < in_files_number; i++) {
				FileInputStream in = new FileInputStream((File)files.get(i));
				ins.add(in);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return ins;
	}
	
	// 将一个文件分成多份
	public boolean cutImageIntoPieces(){
		File file = new File(src_img_path);		
		InputStream in = null;
		// 读文件
		byte[] head = new byte[54];
		
		try {
			
			in = new FileInputStream(file);
			int byteread = 0;
			int defaultvalue = 0;
			byteread = in.read(head);
			outs = createOutPutStream();
			// 输出流没有创建成功
			if (outs==null) {
				// 返回错误
				return false;
			}
			// 头文件写入number个影子图像里
			for (int i = 0; i < out_files_number; i++) {
				((FileOutputStream)outs.get(i)).write(head);
			}
			
			int count = 0;
			// 生成密钥
			generateKey();
			while ((byteread=in.read()) != -1) {
				count = count % out_files_number;
				// 分发到其余文件里
				// 对这8位进行加密
				byteread = encodeByte(byteread);
				
				for (int i = 0; i < out_files_number; i++) {
					if (count == i) {
						((FileOutputStream)outs.get(i)).write(byteread);
					}else {
						((FileOutputStream)outs.get(i)).write(defaultvalue);
					}
				}
				count++;
			}
			
			// 关闭输入输出流
			for (int i = 0; i < out_files_number; i++) {
				((FileOutputStream)outs.get(i)).close();
			}
			in.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean mergeImagesToOne(){
		
		File dec_file;
		FileOutputStream out;
		byte[] head = new byte[54];
		
		try {
			int byteread = 0;
			ins = createInPutStream();
			// 输入流没有成功创建
			if (ins==null) {
				return false;
			}
			dec_file = new File(gen_img_path);
			out = new FileOutputStream(dec_file);
			byteread = ((FileInputStream)ins.get(0)).read(head);
			out.write(head);
			// 其余的图片指针位置移动
			for (int i = 1; i < in_files_number; i++) {
				((FileInputStream)ins.get(i)).skip(54);
			}
			while ((byteread = ((FileInputStream)ins.get(0)).read()) != -1) {
				for (int i = 1; i < in_files_number; i++) {
					// 位操作
					byteread |= ((FileInputStream)ins.get(i)).read();
				}
				// 对byteread进行解密
				byteread = decodeByte(byteread);
				
				out.write(byteread);
			}
			
			// 关闭输入流
			for (int i = 0; i < in_files_number; i++) {
				((FileInputStream)ins.get(i)).close();
			}
			out.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

}
