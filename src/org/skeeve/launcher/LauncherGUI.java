package org.skeeve.launcher;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import javazoom.jl.player.Player;

public class LauncherGUI 	extends JFrame
							implements Runnable{
	
	String gameDirectory;
	public static Player player = null;
	static private final int BOR = 10;
	private ExitAction exitAction;
	AddServerGUI addServerGUI;
	SetServerGUI setServerGUI;
	JComboBox realmlist;
	final JCheckBox thisServer ;
	StartAction startAction;
	Box mainBox;
	JRootPane root = new JRootPane();
	AddServer addServer;
	
	/**Пользовательский интерфес программы. Выбор realm. 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void run(){
		if (Options.isFirstLoad == true){
			Options.gameDirectory = gameDirectoryChange();
			addServerGUI = new AddServerGUI(true);
			addServerGUI.setVisible(true);
		}
	}
	public LauncherGUI(){
		super("Skeeve Launcher");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		exitAction = new ExitAction();
		startAction = new StartAction();
		addServer = new AddServer("Добавить...");
		setRootPane(root);

		Box labelBox = Box.createHorizontalBox(); //Бокс с лэйблом
		JButton addButton = new JButton(addServer);
		addButton.setPreferredSize(new Dimension(110, 23));
		JLabel serverSelection = new JLabel("Выбор сервера:");
		labelBox.add(serverSelection);
		labelBox.add(Box.createHorizontalGlue());
		labelBox.add(addButton);
		labelBox.setBorder(BorderFactory.createEmptyBorder(BOR, BOR, 0, BOR));

		Box realmlistBox = Box.createHorizontalBox(); //Бокс с листом
		
		if (!Options.isFirstLoad){
			realmlist = new JComboBox<Realm>(Options.loadPossibleRealms());
		} else {
			realmlist = new JComboBox<Realm>();
			}
		realmlist.setPreferredSize(new Dimension(300, 20));
		if (Options.defaultServerFlag){
			realmlist.setSelectedIndex(Options.defaultServer);
		}
		realmlistBox.add(realmlist);
		realmlistBox.setBorder(BorderFactory.createEmptyBorder(0, BOR, 0, BOR));
		
		Box buttonBox = Box.createHorizontalBox(); //Бокс с кнопками
		JButton startButton = new JButton(startAction);
		startButton.setPreferredSize(new Dimension(90, 23));
		JButton finishButton = new JButton(exitAction);
		finishButton.setPreferredSize(startButton.getPreferredSize());
		JButton editButton = new JButton("Править");
		editButton.setPreferredSize(startButton.getPreferredSize());
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				setServerGUI = new SetServerGUI();
				setServerGUI.setVisible(true);
			}
		});
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(editButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(finishButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(startButton);
		buttonBox.setBorder(BorderFactory.createEmptyBorder(0, BOR, 0, BOR));
		Box bottomBox = Box.createHorizontalBox(); //Нижний бокс
		thisServer = new JCheckBox("Использовать данный сервер по умолчанию");
		thisServer.setSelected(Options.defaultServerFlag);
		bottomBox.add(thisServer);
		bottomBox.add(Box.createHorizontalGlue());
		bottomBox.setBorder(BorderFactory.createEmptyBorder(BOR/2, BOR, BOR, BOR));
		
		JMenuBar menuBar = new JMenuBar();	//Добавление меню на фрейм
		JMenu fileMenu = new JMenu("Файл");
		JMenuItem addMenuItem = new JMenuItem(addServer);
		JMenuItem delMenuItem = new JMenuItem("Удалить выбранный сервер из списка");
			delMenuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					int i = realmlist.getSelectedIndex();
					realmlist.removeItem(realmlist.getSelectedItem());
					Options.deleteServer(i);					
				}
			});
		JMenuItem exitMenuItem = new JMenuItem(exitAction);
		JMenuItem dirMenuItem = new JMenuItem("Изменить директорию игры");
			dirMenuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					gameDirectoryChange();
				}
			});
		fileMenu.add(addMenuItem);
		fileMenu.add(delMenuItem);
		fileMenu.add(dirMenuItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		
		mainBox = Box.createVerticalBox(); //Заполнение основной панели
		mainBox.add(labelBox);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(realmlistBox);
		mainBox.add(Box.createVerticalStrut(15));
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(buttonBox);
		mainBox.add(Box.createVerticalStrut(15));
		mainBox.add(new JSeparator());
		mainBox.add(bottomBox);
		
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if (thisServer.isSelected()){
					Options.setDefaultServer(realmlist.getSelectedIndex());
					Options.setDefaultServerFlag(true);
					} else {
					Options.setDefaultServerFlag(false);
				}
				String eggCheck = realmlist.getSelectedItem().toString();
				eggCheck = eggCheck.substring(0, 6);
				if (eggCheck.equals("Bortel")||eggCheck.equals("bortel")||eggCheck.equals("бортел")||
						eggCheck.equals("Бортел")){
					playSparta("http://dl.dropbox.com/u/22326796/thisissparta");
				}
				startWow();
				System.exit(0);
			}
		});
		
		realmlist.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if (realmlist.getSelectedIndex()==Options.defaultServer){
					thisServer.setSelected(true);
				}else{
					thisServer.setSelected(false);
				}
				setRealmlistWtf();
			}
		});
		//TODO

		
		root.setDefaultButton(startButton);
		
		setLocationRelativeTo(null);
		getContentPane().add(mainBox);
		pack();
		setResizable(false);
		setVisible(true);
	}
	
	class ExitAction extends AbstractAction {
	
		private static final long serialVersionUID = 1L;

		ExitAction(){
			this("Выход");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		}
		
		ExitAction(String name){
			putValue(Action.NAME, name);
			
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		}
		
		public void actionPerformed(ActionEvent event) {
			System.exit(0); 
		}
	}
	
	class SetAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		SetAction(){
			this("Ок");
		}
		
		SetAction(String name){
			putValue(Action.NAME, name);
		}
		
		public void actionPerformed(ActionEvent event) {
			System.exit(0); 
		}
	}
	
	class CanselAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		CanselAction(){
			this("Отмена");
		}
		
		CanselAction(String name){
			putValue(Action.NAME, name);
		}
		
		public void actionPerformed(ActionEvent event) {
			addServerGUI.dispose();
		}
	}
	
	class SetCanselAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		SetCanselAction(){
			this("Отмена");
		}
		
		SetCanselAction(String name){
			putValue(Action.NAME, name);
		}
		
		public void actionPerformed(ActionEvent event) {
			setServerGUI.dispose();
		}
	}
	class AddServer extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		AddServer(){
			this("Добавить сервер");
		}
		
		AddServer(String name){
			putValue(Action.NAME, name);
		}
		
		public void actionPerformed(ActionEvent event) {
			if (addServerGUI != null){
				addServerGUI.dispose();
			}
			addServerGUI = new AddServerGUI(Options.isFirstLoad);
			addServerGUI.setVisible(true);
		}
	}
	
	class StartAction extends AbstractAction{
		StartAction(){
			this("Ок");
		}
		
		StartAction(String name){
			putValue(Action.NAME, name);
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (thisServer.isSelected()){
				Options.setDefaultServer(realmlist.getSelectedIndex());
				Options.setDefaultServerFlag(true);
				} else {
				Options.setDefaultServerFlag(false);
			}
			String eggCheck = realmlist.getSelectedItem().toString();
			eggCheck = eggCheck.substring(0, 6);
			if (eggCheck.equals("Bortel")||eggCheck.equals("bortel")||eggCheck.equals("бортел")||
					eggCheck.equals("Бортел")){
				playSparta("http://dl.dropbox.com/u/22326796/thisissparta");
			}
			startWow();
			
		}
		
	}
	
	class AddServerGUI extends JFrame{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JTextField nameField;
		JTextField realmlistField;
		CanselAction canselAction;
		static private final int BOR = 12;
		
		AddServerGUI(final boolean isFirstLoad){
			super();

			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			canselAction = new CanselAction("Отмена");
			this.setTitle("Введите, пожалуйста, название и реалмлист вашего сервера");
			
			Box box_0 = Box.createHorizontalBox();
			JLabel infoLabel = new JLabel("Ниже укажите, пожалуйста, название сервера, " +
					"на котором собираетесь играть (опционально) и реалмлист " +
					"данного сервера (обязательно). ");
			box_0.add(infoLabel);
			
			Box box_1 = Box.createHorizontalBox();
			JLabel nameLabel = new JLabel("Название сервера:");
			nameField = new JTextField(40);
			box_1.add(nameLabel);
			box_1.add(Box.createHorizontalStrut(19));
			box_1.add(Box.createHorizontalGlue());
			box_1.add(nameField);
			
			Box box_2 = Box.createHorizontalBox();
			JLabel realmlistLabel = new JLabel("Реалмлист сервера:");
			realmlistField = new JTextField(40);
			realmlistField.setText("set realmlist ");
			box_2.add(realmlistLabel);
			box_2.add(Box.createHorizontalStrut(10));
			box_2.add(Box.createHorizontalGlue());
			box_2.add(realmlistField);
			
			Box box_3 = Box.createHorizontalBox();
			JButton canselButton = new JButton(canselAction);
			canselButton.setPreferredSize(new Dimension(80, 23));
			JButton okButton = new JButton("Ok");
			okButton.setPreferredSize(canselButton.getPreferredSize());
				okButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						Realm newRealm = new Realm(nameField.getText(), realmlistField.getText());
						Options.addServer(newRealm);
						realmlist.addItem(newRealm);
						addServerGUI.dispose();
						if (isFirstLoad){
						}
					}
				});
			box_3.add(Box.createHorizontalGlue());
			box_3.add(canselButton);
			box_3.add(Box.createHorizontalStrut(10));
			box_3.add(okButton);
			
			Box mainBox = Box.createVerticalBox();
			mainBox.setBorder(BorderFactory.createEmptyBorder(BOR, BOR, BOR, BOR));
			if (isFirstLoad == true){
				this.setTitle("Новый сервер");
				mainBox.add(box_0);
				mainBox.add(Box.createVerticalStrut(17));
			}
			mainBox.add(box_1);
			mainBox.add(Box.createVerticalStrut(12));
			mainBox.add(box_2);
			mainBox.add(Box.createVerticalStrut(17));
			mainBox.add(box_3);
			
			mainBox.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
			mainBox.getActionMap().put("cansel", canselAction);
			
			bind("ESCAPE", "canselAdding", canselAction, mainBox);
			root.setDefaultButton(okButton);
			
			setContentPane(mainBox);
			pack();
			setLocationRelativeTo(null);
			setResizable(false);
		}
	}
	
	class SetServerGUI extends JFrame{ 
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JTextField nameField;
		JTextField realmlistField;
		SetCanselAction canselAction;
		static private final int BOR = 12;
		SetAction setAction;
		
		SetServerGUI(){
			super();
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			canselAction = new SetCanselAction("Отмена");
			setAction = new SetAction();
			this.setTitle("Правка");
					
			Box box_1 = Box.createHorizontalBox();
			JLabel nameLabel = new JLabel("Название сервера:");
			nameField = new JTextField(40);
			nameField.setText(getServerName());
			box_1.add(nameLabel);
			box_1.add(Box.createHorizontalStrut(19));
			box_1.add(Box.createHorizontalGlue());
			box_1.add(nameField);
			
			Box box_2 = Box.createHorizontalBox();
			JLabel realmlistLabel = new JLabel("Реалмлист сервера:");
			realmlistField = new JTextField(40);
			realmlistField.setText(getServerRealmlist());
			box_2.add(realmlistLabel);
			box_2.add(Box.createHorizontalStrut(10));
			box_2.add(Box.createHorizontalGlue());
			box_2.add(realmlistField);
			
			Box box_3 = Box.createHorizontalBox();
			JButton canselButton = new JButton(canselAction);
			canselButton.setPreferredSize(new Dimension(80, 23));
			JButton okButton = new JButton("Ok");
			okButton.setPreferredSize(canselButton.getPreferredSize());
				okButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						int i = realmlist.getSelectedIndex();
						realmlist.removeItemAt(i);
						realmlist.insertItemAt(nameField.getText()+" ("+ realmlistField.getText()+")"
								, i);
						realmlist.setSelectedIndex(i);
						Options.editServer(realmlist.getSelectedIndex(),
								getServerName(), getServerRealmlist());
						setServerGUI.dispose();
					}
				});
			box_3.add(Box.createHorizontalGlue());
			box_3.add(canselButton);
			box_3.add(Box.createHorizontalStrut(10));
			box_3.add(okButton);
			
			Box mainBox = Box.createVerticalBox();
			mainBox.setBorder(BorderFactory.createEmptyBorder(BOR, BOR, BOR, BOR));
			mainBox.add(box_1);
			mainBox.add(Box.createVerticalStrut(12));
			mainBox.add(box_2);
			mainBox.add(Box.createVerticalStrut(17));
			mainBox.add(box_3);
			
			bind("ESCAPE", "canselSetting", canselAction, mainBox);
			bind("ENTER", "set", setAction, mainBox);
			root.setDefaultButton(okButton);
			
			setContentPane(mainBox);
			pack();
			setLocationRelativeTo(null);
			setResizable(false);
		}
	}
	String gameDirectoryChange(){
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("WTF Files", "wtf");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showDialog(null, "Укажите путь к файлу \"realmlis.wtf\"");
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	gameDirectory = chooser.getSelectedFile().getPath();
	    	Options.userPrefs.put("GameDirectory", gameDirectory);
	    	Options.isFirstLoad = false;
	    }
	    return gameDirectory;
	}
	
	private void startWow(){
		String command = Options.gameDirectory
				.substring(0, Options.gameDirectory.indexOf("\\Data"))+"\\Wow.exe";

		try {
			Process proc = Runtime.getRuntime().exec(command);
			try {
				proc.waitFor();
			} 
			catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null,"Не удалось запустить приложение!");
				e.printStackTrace();
			}
		}catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Что-то не срослось");
			e.printStackTrace();
		}
	}
	
	public void playSparta(String address){
		try {
			URL MP3URL = new URL(address);
		    URLConnection connection = MP3URL.openConnection();
		    BufferedInputStream bis = new BufferedInputStream(connection
		        .getInputStream()); 
			player = new Player(bis);
			player.play();
		}
		catch(Exception ex){
				JOptionPane.showMessageDialog(null,"This is BOOOOOORTEEEEEEEL!!!!111one!");
		}
	}
	
	private void setRealmlistWtf(){
		String currentServer = (String) realmlist.getSelectedItem().toString();
		currentServer = currentServer.substring(currentServer.indexOf('(')+1,
				currentServer.indexOf(')'));
		try {
			FileOutputStream wtf = new FileOutputStream(Options.gameDirectory);
			try {
				wtf.write(currentServer.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Проверьте правильность указания директории игры!");
			e1.printStackTrace();
		}
	}
	

	private String getServerName(){
		return realmlist.getSelectedItem().toString()
		.substring(0, realmlist.getSelectedItem().toString().indexOf('(')-1);
	}
	
	private String getServerRealmlist(){
		return realmlist.getSelectedItem().toString()
				.substring(realmlist.getSelectedItem().toString().indexOf('(')+1,
				realmlist.getSelectedItem().toString().indexOf(')'));
	}
	private void bind(String key, String name, Action action, JComponent component){
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), name);
		component.getActionMap().put(name, action);		
	}
}
