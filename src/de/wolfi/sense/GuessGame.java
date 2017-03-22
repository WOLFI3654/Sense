package de.wolfi.sense;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GuessGame extends JFrame  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2852444261481430807L;
	private JPanel contentPane;

	private int right, wrong;
	private volatile boolean protect = false;
	/**
	 * Create the frame.
	 */
	SecureRandom random = new SecureRandom();
	ImageIcon tar;
	ArrayList<ImageIcon> fls = new ArrayList<>();
	private JTextField textField;
	private JLabel panel_1;
	private JLabel panel_2;
	public GuessGame(File target, ArrayList<File> files) throws IOException {
		setSize(new Dimension(1024, 900));
		tar = new ImageIcon(ImageIO.read(target));
		
		files.forEach(f->{
			try {
				fls.add(new ImageIcon(ImageIO.read(f)));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		});
		fls.remove(tar);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		panel_1 = new JLabel();
		panel_1.setBackground(Color.DARK_GRAY);
		
		panel_2 = new JLabel();
		panel_2.setBackground(Color.DARK_GRAY);
		
		
		
		contentPane.setLayout(new GridLayout(2, 3, 0, 0));
		contentPane.add(panel_1);
		contentPane.add(panel_2);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				System.out.println(protect);
				if(protect) return;
				protect = true;
				System.out.println(e.getKeyCode());
				if(random.nextBoolean()) {
					panel_2.setIcon(tar);
					panel_1.setIcon(fls.get(random.nextInt(fls.size())));
					if(e.getKeyCode() == KeyEvent.VK_D)
						right++;
					else
						wrong ++;
				} else {
					panel_1.setIcon(tar);
					panel_2.setIcon(fls.get(random.nextInt(fls.size())));
					if(e.getKeyCode() == KeyEvent.VK_A)
						right++;
					else
						wrong ++;
				}
				panel_1.repaint();
				panel_2.repaint();
				Thread t = new Thread(new Runnable() {
					public void run() {
						random.setSeed(random.generateSeed(1));
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						panel_1.setIcon(null);
						panel_2.setIcon(null);
						protect = false;
					}
				});
				updateCounter();
				t.start();
				
			}
		});
		contentPane.add(textField);
		
	}

	private void updateCounter() {
		StringBuilder v = new StringBuilder();
		v.append(right); v.append('/'); v.append(right+wrong);v.append(' ');v.append(':'); v.append(wrong);
		textField.setText(v.toString());
		
	}
}
