package medeye.GUI;

import medeye.MedEye;
import medeye.imaging.ImageUtil;
import medeye.medical.DrugUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static medeye.MedEye.DRUG_DATABASE;

public class MainFrame extends JFrame{

    private AltTab altTab;
    private SETab seTab;
    public MainFrame(String title) {
        super(title);

        altTab = new AltTab();
        seTab = new SETab();

        //Set layout manager (how to arrange components on the frame
        setLayout(new BorderLayout());


        //Create Swing Component
        Dimension d = new Dimension();

        JPanel dArea = new JPanel();
        dArea.setFont(new Font("Arial", 0, 36));
        dArea.setVisible(true);
        dArea.setAutoscrolls(false);

        BufferedImage img = ImageUtil.loadImageFromPath(MedEye.BASE_IMAGE_DIR);
        img = resize(img, 200, 200);
        JLabel image = new JLabel(new ImageIcon(img));
        image.setSize(new Dimension(200,200));
        dArea.add(image,BorderLayout.CENTER);
        ImageUtil.DrugTriplet trip = DrugUtil.processDrugs(DRUG_DATABASE, MedEye.updatedTarget).get(0);
        JLabel price = new JLabel("$" + trip.getUnitPrice() + " / " + trip.getUnit());
        dArea.add(price, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(dArea);
        scrollPane.setPreferredSize(new Dimension(500,300));
        dArea.setAlignmentX(0);
        dArea.setAlignmentY(0);

        JTabbedPane tabbedPane = new JTabbedPane();
        d.setSize(500,500);
        tabbedPane.setPreferredSize(d);

        tabbedPane.addTab("Known side effects of this drug",seTab);
        tabbedPane.addTab("Alternatives",altTab);

        add(scrollPane, BorderLayout.NORTH);
        add(tabbedPane,BorderLayout.CENTER);
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
