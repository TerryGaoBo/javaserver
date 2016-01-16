package com.dol.cdf.tools.modelGenerate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.dol.cdf.tools.modelGenerate.model.Excel2JavaPropertiesModel;
import com.dol.cdf.tools.modelGenerate.util.ExampleFileFilter;
import com.dol.cdf.tools.modelGenerate.util.ModelGenUtil;
import com.dol.cdf.tools.modelGenerate.util.WarningPanle;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class GeneratorMain extends javax.swing.JFrame {
	private JLabel jLabel2;
	private JButton jButton1;
	private JCheckBox jcb3;
	private JCheckBox jcb2;
	private JCheckBox jcb1;
	private JLabel jLabel4;
	private JLabel jLabel3;
	private JButton jButton3;
	private JButton jButton2;
	private JTextField jTextField1;
	private ButtonGroup buttonGroup1;
	private JLabel jLabel1;
	private WarningPanle warningPanle;
	
	private List<Excel2JavaPropertiesModel> models;
	private  String configFile = "tool/tools.properties";
	
	private Properties properties;
	private String outFilePath;
	private String default_read_path;
	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GeneratorMain inst = new GeneratorMain();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public GeneratorMain() {
		super();
		initGUI();
		initWarningPanle();
		loadProperties();
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent evt) {
					thisWindowClosed(evt);
				}
				public void windowIconified(WindowEvent evt) {
					System.out.println("this.windowIconified, event="+evt);
					//TODO add your code for this.windowIconified
				}
			});
			{
				jLabel1 = new JLabel();
				getContentPane().add(jLabel1);
				getContentPane().add(getJTextField1());
				getContentPane().add(getJLabel2());
				getContentPane().add(getJButton1());
				getContentPane().add(getJButton2());
				getContentPane().add(getJButton3());
				getContentPane().add(getJLabel3());
				getContentPane().add(getJLabel4());
				getContentPane().add(getJCheckBox4());
				getContentPane().add(getJCheckBox5());
				getContentPane().add(getJCheckBox6());
				jLabel1.setText("excel\u751f\u6210java\u5bf9\u8c61");
				jLabel1.setBounds(92, 33, 428, 64);
				jLabel1.setFont(new java.awt.Font("微软雅黑",0,36));
				jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
			}
			pack();
			this.setSize(671, 449);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private ButtonGroup getButtonGroup1() {
		if(buttonGroup1 == null) {
			buttonGroup1 = new ButtonGroup();
		}
		return buttonGroup1;
	}

	private JTextField getJTextField1() {
		if(jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setBounds(347, 197, 188, 30);
			jTextField1.setFont(new java.awt.Font("微软雅黑",0,24));
			jTextField1.setEditable(false);
			jTextField1.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent evt) {
					jTextField1KeyTyped(evt);
				}
			});
		}
		return jTextField1;
	}
	
	private JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("2\u3001\u751f\u6210java\u6587\u4ef6\u7684\u7c7b\u540d");
			jLabel2.setBounds(51, 196, 303, 30);
			jLabel2.setFont(new java.awt.Font("微软雅黑",0,24));
		}
		return jLabel2;
	}
	
	private JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("choose file");
			jButton1.setBounds(382, 131, 153, 30);
			jButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});
		}
		return jButton1;
	}
	
	private JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("choose directory");
			jButton2.setBounds(397, 255, 138, 36);
			jButton2.setEnabled(false);
			jButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton2ActionPerformed(evt);
				}
			});
		}
		return jButton2;
	}
	
	private JButton getJButton3() {
		if(jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText("generate");
			jButton3.setBounds(219, 319, 185, 45);
			jButton3.setFont(new java.awt.Font("微软雅黑",0,28));
			jButton3.setEnabled(false);
			jButton3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton3ActionPerformed(evt);
				}
			});
		}
		return jButton3;
	}
	
	private void jButton1ActionPerformed(ActionEvent evt) {
		selectedFile();
	}
	
	private void jButton2ActionPerformed(ActionEvent evt) {
		selectedDirectory();
	}
	
	private void jButton3ActionPerformed(ActionEvent evt) {
		ModelGenUtil.writeJavaFile(this.outFilePath,this.jTextField1.getText(), models);
		properties.setProperty("default_gen_path", this.outFilePath);
		properties.setProperty("default_read_path", default_read_path);
		try {
			OutputStream fos = new FileOutputStream(configFile);
			properties.store(fos, "desc");
			fos.close();
		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		this.jcb1.setSelected(false);
		this.jcb2.setSelected(false);
		this.jcb3.setSelected(false);
		
		this.jButton2.setEnabled(false);
		this.jButton3.setEnabled(false);
		this.jTextField1.setText("");
		this.jTextField1.setEditable(false);
		warningPanle.showMessage("生成成功！");
	}
	public  void selectedFile() {
		// 设置普通参数
		JFrame dialogFrame = new JFrame(); // 弹出窗口
		// 设置打开文件的扩展名 开始
		JFileChooser c = new JFileChooser(properties.getProperty("default_read_path"));
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("xls");
		filter.setDescription("excel文件");
		c.setFileFilter(filter);
		int rVal = c.showOpenDialog(dialogFrame);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			File file=c.getSelectedFile();
			models=ModelGenUtil.loadExcel(file);
			if(models==null||models.size()==0){
				warningPanle.showMessage("读取失败，请确定文件存在且没有错误！");
			}else{
				String name=file.getName();
				name=name.substring(0, name.lastIndexOf("."));
				name=name.substring("schema_".length());
				default_read_path=file.getParentFile().getAbsolutePath();
				jcb1.setSelected(true);
				jTextField1.setEditable(true);
				jTextField1.setText(name);
				jcb2.setSelected(true);
				jButton2.setEnabled(true);
				jButton3.setEnabled(true);
			}
		}
	}
	private void initWarningPanle(){
		warningPanle=new WarningPanle(this,true);
		warningPanle.setVisible(false);
	}
	
	public  void selectedDirectory() {
		// 设置普通参数
		JFrame dialogFrame = new JFrame(); // 弹出窗口
		// 设置打开文件的扩展名 开始
		JFileChooser c = new JFileChooser(properties.getProperty("default_gen_path"));
		c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rVal = c.showOpenDialog(dialogFrame);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			outFilePath=c.getSelectedFile().getPath();
			if(outFilePath==null||outFilePath.length()==0){
				warningPanle.showMessage("选取路径失败！");
			}else{
				jcb3.setSelected(true);
				jButton3.setEnabled(true);
			}
		}
	}
	
	private JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("1\u3001\u9009\u62e9excel");
			jLabel3.setBounds(51, 121, 153, 30);
			jLabel3.setFont(new java.awt.Font("微软雅黑",0,24));
		}
		return jLabel3;
	}
	
	private JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("3\u3001\u9009\u62e9\u751f\u6210java\u6587\u4ef6\u7684\u5730\u5740");
			jLabel4.setFont(new java.awt.Font("微软雅黑",0,24));
			jLabel4.setBounds(51, 257, 313, 34);
		}
		return jLabel4;
	}

	private JCheckBox getJCheckBox4() {
		if(jcb1 == null) {
			jcb1 = new JCheckBox();
			jcb1.setEnabled(false);
			jcb1.setBounds(573, 133, 17, 18);
		}
		return jcb1;
	}
	
	private JCheckBox getJCheckBox5() {
		if(jcb2 == null) {
			jcb2 = new JCheckBox();
			jcb2.setBounds(573, 204, 17, 17);
			jcb2.setEnabled(false);
		}
		return jcb2;
	}
	
	private JCheckBox getJCheckBox6() {
		if(jcb3 == null) {
			jcb3 = new JCheckBox();
			jcb3.setBounds(573, 264, 22, 17);
			jcb3.setEnabled(false);
		}
		return jcb3;
	}
	
	private void jTextField1KeyTyped(KeyEvent evt) {
		if(this.jTextField1.getText()!=null&&
				this.jTextField1.getText().length()>0){
			this.jcb2.setSelected(true);
			jButton2.setEnabled(true);
		}else{
			this.jButton2.setEnabled(false);
			this.jcb2.setSelected(false);
		}
		
	}
	
	private void thisWindowClosed(WindowEvent evt) {
		System.exit(0);
	}
	private  void loadProperties(){
		properties=new Properties();
		try {
			properties.load(ClassLoader.getSystemResourceAsStream(configFile));
			outFilePath=properties.getProperty("default_gen_path");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
