import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class UI extends JFrame{
	
	private JPanel panel;
	private JButton encrypt;
	private JButton decode;
	private JTextField encode_number;
	private JTextField decode_number;
	private JLabel label_encode_number;
	private JLabel label_decode_number;
	
	private HandleImage handleImage;
	
	public final int MARGIN_LEFT = 10;
	public final int MARGIN_TOP = 10;
	public final int TEXTVIEW_HEIGHT = 20;
	public final int TEXTVIEW_WIDTH = 80;
	public final int BUTTON_HEIGHT = 20;
	public final int BUTTON_WIDTH = 80;
	public final int LABEL_WIDTH = 100;
	public final int LABEL_HEIGHT = 20;
	
	public UI(){
		this.setSize(350, 300);
		this.setLocation(50, 30);
		initPanel();
	}
	
	public void initPanel(){
		
		panel = new JPanel();
		encrypt = new JButton("加密");
		decode = new JButton("解密");
		encode_number = new JTextField();
		decode_number = new JTextField();
		label_encode_number = new JLabel("加密数量");
		label_decode_number = new JLabel("解密数量");
		
		panel.setLayout(null);
		
		panel.add(label_encode_number);
		label_encode_number.setBounds(MARGIN_LEFT, MARGIN_TOP, LABEL_WIDTH, LABEL_HEIGHT);
		
		panel.add(encode_number);
		encode_number.setBounds(MARGIN_LEFT*2+LABEL_WIDTH, MARGIN_TOP, TEXTVIEW_WIDTH, TEXTVIEW_HEIGHT);
		
		panel.add(encrypt);
		encrypt.setBounds(MARGIN_LEFT, MARGIN_TOP*2+TEXTVIEW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
		encrypt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int files_number = 0;
				try {
					files_number = Integer.parseInt(encode_number.getText());
				} catch (Exception e2) {
					// TODO: handle exception
					files_number = -1;
				}
				if (files_number <= 0) {
					// 返回错误
					JOptionPane.showMessageDialog(panel, "输入不合法", "提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// 没有这个对象的话创建一个
				if (handleImage==null) {
					handleImage = new HandleImage();
				}
				handleImage.initOutNumber(files_number);
				handleImage.cutImageIntoPieces();
				JOptionPane.showMessageDialog(panel, "分解完成");
			}
		});
		
		panel.add(label_decode_number);
		label_decode_number.setBounds(MARGIN_LEFT, MARGIN_TOP*3+TEXTVIEW_HEIGHT*2, LABEL_WIDTH, LABEL_HEIGHT);
		
		panel.add(decode_number);
		decode_number.setBounds(MARGIN_LEFT*2+LABEL_WIDTH, MARGIN_TOP*3+TEXTVIEW_HEIGHT*2, TEXTVIEW_WIDTH, TEXTVIEW_HEIGHT);
		
		panel.add(decode);
		decode.setBounds(MARGIN_LEFT, MARGIN_TOP*4+TEXTVIEW_HEIGHT*3, BUTTON_WIDTH, BUTTON_HEIGHT);
		decode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int files_number = 0;
				try {
					files_number = Integer.parseInt(decode_number.getText());
				} catch (Exception e2) {
					// TODO: handle exception
					files_number = -1;
				}
				// 
				if (files_number <= 0) {
					JOptionPane.showMessageDialog(panel, "输入不合法", "提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(handleImage==null){
					JOptionPane.showMessageDialog(panel, "还木有加密", "提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!handleImage.checkFiles(files_number)) {
					JOptionPane.showMessageDialog(panel, "实际图片数量少于合并数量", "提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// 输入合并数量并开始合并
				handleImage.initInNumber(files_number);
				handleImage.mergeImagesToOne();
				JOptionPane.showMessageDialog(panel, "合并完成");
			}
		});
		
		this.add(panel);
	}
}
