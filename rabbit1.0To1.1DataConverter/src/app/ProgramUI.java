package app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The user interface for this program.
 */
public class ProgramUI extends JFrame {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   */
  public ProgramUI() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationByPlatform(true);
    setTitle("Rabbit Data Converter");

    final JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.append(
        "This tool converts Rabbit 1.0 data files to the 1.1 format.\n\n" +
    		"Please uninstall Rabbit 1.0 from Eclipse by removing\n" +
    		"  - rabbit.core_1.0.0.xxx.jar and\n" +
    		"  - rabbit.ui_1.0.0_xxx.jar\n" +
    		"before running this tool.\n\n");
    add(textArea, BorderLayout.CENTER);

    final JButton button = new JButton();
    button.setText("Start");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        button.setEnabled(false);
        textArea.append("Running...\n");
        // OK to run this in UI thread, workload is small
        Program.run();
        textArea.append("Done! All operations have finished.\n");
        
        button.setEnabled(true);
        button.setText("Exit");
        button.removeActionListener(this);
        button.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        });
      }
    });
    add(button, BorderLayout.SOUTH);

    setSize(500, 400);
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      
    } catch (ClassNotFoundException e) {
      System.err.println(e.getMessage());
    } catch (InstantiationException e) {
      System.err.println(e.getMessage());
    } catch (IllegalAccessException e) {
      System.err.println(e.getMessage());
    } catch (UnsupportedLookAndFeelException e) {
      System.err.println(e.getMessage());
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        ProgramUI ui = new ProgramUI();
        ui.setVisible(true);
      }
    });
  }
}
