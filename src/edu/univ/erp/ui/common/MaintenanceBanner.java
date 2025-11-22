package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * A simple, reusable banner to show when maintenance mode is active.
 */
public class MaintenanceBanner extends JPanel {

    public MaintenanceBanner(String message) {
        // Set the panel properties
        this.setBackground(Color.YELLOW);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Create the label
        JLabel bannerLabel = new JLabel(message);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bannerLabel.setForeground(Color.BLACK);
        
        // Add the label to this panel
        this.add(bannerLabel);
    }

    /**
     * A static helper to check for maintenance mode and add the banner.
     * * @param frame The JFrame to add the banner to.
     */
    public static void checkAndAddBanner(JFrame frame) {
        // We need to check the status. We can (re)use the adminService for this.
        edu.univ.erp.service.adminService service = new edu.univ.erp.service.adminService();
        
        if (service.isMaintenanceModeOn()) {
            MaintenanceBanner banner = new MaintenanceBanner(
                "System is in MAINTENANCE MODE. Functionality is read-only."
            );
            
            // Set bounds to be at the top of the frame
            banner.setBounds(0, 0, frame.getWidth(), 30);
            
            // Add the banner to the frame
            frame.add(banner, 0); // Add at index 0 to make it appear on top
        }
    }
}