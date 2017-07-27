package com.mercury.platform.ui.adr.components.panel.tree.main;

import com.mercury.platform.shared.config.descriptor.adr.AdrProgressBarDescriptor;
import com.mercury.platform.ui.adr.components.panel.tree.AdrNodePanel;
import com.mercury.platform.ui.adr.components.panel.ui.MercuryTracker;
import com.mercury.platform.ui.adr.routing.AdrComponentDefinition;
import com.mercury.platform.ui.adr.routing.AdrComponentOperations;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.dialog.DialogCallback;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;

import javax.swing.*;
import java.awt.*;


public class AdrProgressBarNodePanel extends AdrNodePanel<AdrProgressBarDescriptor> {
    public AdrProgressBarNodePanel(AdrProgressBarDescriptor descriptor, boolean inner) {
        super(descriptor,inner);
    }

    @Override
    protected void update() {
        /*NOP*/
    }

    @Override
    public void createUI() {
        JPanel root = this.componentsFactory.getJPanel(new FlowLayout(FlowLayout.CENTER));
        root.setBackground(AppThemeColor.SLIDE_BG);
        MercuryTracker tracker = new MercuryTracker(descriptor);
        tracker.setValue((int) ((descriptor.getDuration()/2) * 1000));
        tracker.setFont(new ComponentsFactory().getFont(FontStyle.BOLD, 20));
        tracker.setPreferredSize(new Dimension(180, 36));
        tracker.setShowCase(true);
        this.setPreferredSize(new Dimension(150, 48));
        root.add(tracker);
        this.add(root,BorderLayout.CENTER);
        this.add(this.adrComponentsFactory.getLeftComponentOperationsPanel(this.descriptor),BorderLayout.LINE_START);
        this.add(this.adrComponentsFactory
                .getRightComponentOperationsPanel(this.descriptor),BorderLayout.LINE_END);
    }
}