package main;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import main.Menu;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Menu extends javax.swing.JFrame implements Runnable, ThreadFactory {

	private WebcamPanel panel = null;
	private Webcam webcam = null;

	private static final long serialVersionUID = 6441489157408381878L;
	private static final int QRCODE_KEY_COUNT = 6;
	private Executor executor = Executors.newSingleThreadExecutor(this);

	public Menu() {
		initComponents();
		initWebcam();
	}

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		result_field = new javax.swing.JTextField();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel1 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		result_field.setBorder(null);
		jPanel1.add(result_field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 470, 20));

		jSeparator1.setForeground(new java.awt.Color(126, 167, 206));
		jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 470, 10));

		jLabel1.setForeground(new java.awt.Color(105, 105, 105));
		jLabel1.setText("Resultado");
		jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, -1, -1));

		jPanel2.setBackground(new java.awt.Color(250, 250, 250));
		jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));
		jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 470, 300));

		getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 380));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(() -> {
			new Menu().setVisible(true);
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTextField result_field;
	// End of variables declaration//GEN-END:variables

	private void initWebcam() {
		Dimension size = WebcamResolution.QVGA.getSize();
		webcam = Webcam.getWebcams().get(0); // 0 is default webcam
		webcam.setViewSize(size);

		panel = new WebcamPanel(webcam);
		panel.setPreferredSize(size);
		panel.setFPSDisplayed(true);

		jPanel2.add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 470, 300));

		executor.execute(this);
	}

	@Override
	public void run() {
		String prevText = null; // keeps record of previous text
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Result result = null;
			BufferedImage image = null;

			if (webcam.isOpen()) {
				if ((image = webcam.getImage()) == null) {
					continue;
				}
			}

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			try {
				result = new MultiFormatReader().decode(bitmap);
			} catch (NotFoundException e) {
				// No result...
			}

			if (result != null) {
				result_field.setText(result.getText());
				if (!(result.getText().equals(prevText)))
					processQRCode(result.getText()); // process the QR Code
				prevText = result.getText();
			}
		} while (true);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, "My Thread");
		t.setDaemon(true);
		return t;
	}

	private void processQRCode(String myQRCode) {
		System.out.println(myQRCode);

		// Parse the data from the QR code and validate it

		boolean validQRCode = true;
		String room = null;
		String eventType = null;
		String eventName = null;
		String startTime = null;
		String endTime = null;
		String eventDate = null;
		int keyCount = 0;
		String[] dateParts = null;
		int sday = 0;
		int smonth = 0;
		int syear = 0;
		long qrcodeEndTime = 0, qrcodeStartTime = 0;

		String[] splits = myQRCode.split(":"); // Split the assets
		System.out.println("splits.size: " + splits.length);
		System.out.println("Validating");

		for (String asset : splits) {
			// System.out.println(asset);
			if (asset.equals("Event"))
				keyCount++;
			if (asset.equals("Name"))
				keyCount++;
			if (asset.equals("EventDate"))
				keyCount++;
			if (asset.equals("StartTime"))
				keyCount++;
			if (asset.equals("EndTime"))
				keyCount++;
			if (asset.equals("Room"))
				keyCount++;
		}

		// validation section

		// check we have all our keys in the qr code
		if (keyCount == QRCODE_KEY_COUNT) {
			eventType = splits[1];
			eventName = splits[3];
			eventDate = splits[5];
			startTime = splits[7];
			endTime = splits[9];
			room = splits[11];

			if (eventType.isBlank()) {
				System.out.println("The QR code event type is blank");
				// music(QRCODE_EVENTTYPE_INVALID.wav);
				validQRCode = false;
			}
			if (!(eventType.equals("class") || eventType.equals("meeting"))) {
				System.out.println("The event type " + eventType + " is not recognised from this QR Code");
				validQRCode = false;
			}
			if (eventName.isBlank()) {
				System.out.println("The QR code event name is blank");
				validQRCode = false;
			}
			if (eventDate.isBlank()) {
				System.out.println("The QR code event date is blank");
				validQRCode = false;
			}
			long count = eventDate.chars().filter(ch -> ch == '/').count();
			if (count != 2) {
				System.out.println("The QR code event date does not appear to be of the correct format DD/MM/YYYY");
				validQRCode = false;
			}
			if (eventDate.length() != 10) {
				System.out.println("The QR code event date length does not appear to be correct DD/MM/YYYY");
				validQRCode = false;
			}

			// Extract date
			dateParts = eventDate.split("/");
			sday = Integer.parseInt(dateParts[0]);
			smonth = Integer.parseInt(dateParts[1]);
			syear = Integer.parseInt(dateParts[2]);

			if (startTime.isBlank()) {
				System.out.println("The QR code event start time is blank");
				validQRCode = false;
			}
			count = startTime.chars().filter(ch -> ch == 'h').count();
			if (count != 1) {
				System.out.println(
						"The QR code start date does not appear to be of the correct format xxhyy: " + startTime);
				validQRCode = false;
			}
			if (startTime.length() != 5) {
				System.out.println("The QR code start date length does not appear to be correct (5): " + startTime);
				validQRCode = false;
			}

			// Extract start time in epoch
			String[] startTimeParts = startTime.split("h");
			String str = dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0] + ' ' + startTimeParts[0] + ':'
					+ startTimeParts[1] + ":00";
			ZonedDateTime ldate = LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
					.atZone(ZoneId.of("Europe/Dublin"));
			qrcodeStartTime = ldate.toInstant().toEpochMilli();
			System.out.println("Start Time = " + str + ", epoch time is " + qrcodeStartTime);

			if (endTime.isBlank()) {
				System.out.println("The QR code event end time is blank");
				validQRCode = false;
			}
			count = endTime.chars().filter(ch -> ch == 'h').count();
			if (count != 1) {
				System.out.println("The QR code end date does not appear to be of the correct format xxhyy:" + endTime);
				validQRCode = false;
			}
			if (endTime.length() != 5) {
				System.out.println("The QR code end date length does not appear to be correct (5): " + endTime);
				validQRCode = false;
			}

			// Extract EndTime in epoch
			String[] endTimeParts = endTime.split("h");
			String str2 = dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0] + ' ' + endTimeParts[0] + ':'
					+ endTimeParts[1] + ":00";
			ZonedDateTime ldate2 = LocalDateTime.parse(str2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
					.atZone(ZoneId.of("Europe/Dublin"));
			qrcodeEndTime = ldate2.toInstant().toEpochMilli();
			System.out.println("End Time   = " + str2 + ", epoch time is " + qrcodeEndTime);

			if (room.isBlank()) {
				System.out.println("The QR code room is blank");
				validQRCode = false;
			}
			if (room.length() != 5) {
				System.out.println("The QR code room length does not appear to be correct (5): " + room);
				validQRCode = false;
			}
		} else {
			System.out.println("I'm sorry, I could not read the QR code this time");
			validQRCode = false;
		}

		// process the data in the valid QR code
		if (validQRCode) {
			System.out.println("QR Code is valid");
			// Check date
			if (!(sday == getTodaysDayOfMonth() && smonth == getTodaysMonth() && syear == getTodaysYear())) {
				System.out.println("The event in the QR code is not taking place today");
				File sound = new File("./src/sounds/EventNotToday.wav");
				PlaySound(sound);
			}

			// Get current time
			LocalDateTime ldt = LocalDateTime.now();
			ZonedDateTime zonedDateTime = ldt.atZone(ZoneId.of("Europe/Dublin"));
			long e = zonedDateTime.toInstant().toEpochMilli(); // Then get the epoch on GMT
			Instant i = Instant.ofEpochMilli(e);
			System.out.println(
					"Now = " + ZonedDateTime.ofInstant(i, ZoneId.of("Europe/Dublin")) + ", epoch time is " + e);

			if ((e > qrcodeStartTime) && (e < qrcodeEndTime)) {
				System.out.println("The " + eventType + " has already started");
				File sound = new File("./src/sounds/EventAlreadyStarted.wav");
				PlaySound(sound);
			}
			if ((e > qrcodeStartTime) && (e > qrcodeEndTime)) {
				System.out.println("The " + eventType + " has ended!");
				File sound = new File("./src/sounds/EventFinished.wav");
				PlaySound(sound);
			}
			// check the room
			System.out.println("Room directions follow...");
			if (room.equals("A1002")) {
				System.out.println(
						"Climb the staircase in reception to the first floor, turn right and the room is on your right just before the library");
			} else if (room.equals("E2004")) {
				System.out.println(
						"Leave reception through the left engineering exit.  Climb the two flights of stairs and the room is at the end of the corridor on the right hand side.");
						File sound = new File("./src/sounds/e2004.wav");
						PlaySound(sound);
			} else {
				System.out.println("This room is currently not recognised");
				File sound = new File("./src/sounds/RoomUnknown.wav");
				PlaySound(sound);
			}
		} else {
			System.out.println("This QR Code is not recognised");
			File sound = new File("./src/sounds/QrUnvalid.wav");
			PlaySound(sound);
		}
	}

	public int getTodaysDayOfMonth() {
		final Calendar c = Calendar.getInstance();
		System.out.println("Day of Month = " + c.get(Calendar.DAY_OF_MONTH));
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getTodaysMonth() {
		final Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		month++; // months start at 0
		System.out.println("Month = " + month);
		return (month);
	}

	public int getTodaysYear() {
		final Calendar c = Calendar.getInstance();
		System.out.println("Year = " + c.get(Calendar.YEAR));
		return c.get(Calendar.YEAR);
	}

	private void PlaySound(File Sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Sound));
			clip.start();

			Thread.sleep(clip.getMicrosecondLength() / 1000);
		} catch (Exception e) {
			System.out.println("Exception " + e + " when playing sound");
		}
	}

}
