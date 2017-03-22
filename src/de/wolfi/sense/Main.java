package de.wolfi.sense;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class Main {

	
	private static ArrayList<File> images = new ArrayList<>();
	private static final FileFilter filter =  new FileNameExtensionFilter(
		    "Image files", ImageIO.getReaderFileSuffixes());
	private static final java.io.FileFilter filtre = new java.io.FileFilter() {
	
		@Override
		public boolean accept(File paramFile) {
			
			return filter.accept(paramFile);
		}
	};
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		JFileChooser c = new JFileChooser();
		c.setDialogTitle("Select image folder");
		c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		c.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File paramFile) {
				if(paramFile.isFile()) return false;
				return acceptIntern(paramFile);
			}

			private boolean acceptIntern(File folder){
				for(File f : folder.listFiles()){
					if(f.isDirectory()) return acceptIntern(f);
					if(Main.filter.accept(f)) return true;
				}
				return false;
			}
			@Override
			public String getDescription() {
				return "Image Folders";
			}
		});
		if(c.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			File folder = c.getSelectedFile();
			TreeNode node = sort(createTree(folder));
			
			JTree tree = new JTree(new DefaultTreeModel(node));
			JFrame frame = new JFrame();
			
			frame.setSize(800, 600);
			JSplitPane split = new JSplitPane();
			JScrollPane scroll = new JScrollPane(tree);
		
			split.setRightComponent(scroll);
			split.setLeftComponent(createStartButton(tree));
			frame.add(split);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		
		
	}
	
	
	private static JButton createStartButton(JTree tree) {
		JButton botton = new JButton("Start");
		botton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				File file = (File) ((DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent()).getUserObject();
				try {
					new GuessGame(file, images).setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((JFrame)botton.getParent().getParent().getParent().getParent().getParent()).dispose();
			}
		});
		return botton;
	}


	private static DefaultMutableTreeNode sort(DefaultMutableTreeNode node) {

	    //sort alphabetically
	    for(int i = 0; i < node.getChildCount() - 1; i++) {
	        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
	        String nt = child.getUserObject().toString();

	        for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
	            DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);
	            String np = prevNode.getUserObject().toString();

	            if(nt.compareToIgnoreCase(np) > 0) {
	                node.insert(child, j);
	                node.insert(prevNode, i);
	            }
	        }
	        if(child.getChildCount() > 0) {
	            sort(child);
	        }
	    }

	    //put folders first - normal on Windows and some flavors of Linux but not on Mac OS X.
	    for(int i = 0; i < node.getChildCount() - 1; i++) {
	        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
	        for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
	            DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);

	            if(!prevNode.isLeaf() && child.isLeaf()) {
	                node.insert(child, j);
	                node.insert(prevNode, i);
	            }
	        }
	    }

	    return node;

	}
	
	private static DefaultMutableTreeNode createTree(File folder){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
		for(File file : folder.listFiles(filtre)){
//			if(file.isDirectory()){
//				TreeNode n = createTree(file);
//				System.out.println(n);
//				node.add((MutableTreeNode) n);
//			}else
			
			node.add(file.isDirectory()?createTree(file):new DefaultMutableTreeNode(file));
			if(file.isFile()) images.add(file);
		}
		return node;
	}
}
