package com.ems.ui.components;

import java.awt.Color;
import java.awt.Font;

/**
 * Design tokens and locked color palette for the Employee Management System UI.
 */
public final class UIConstants {

    private UIConstants() {
    }

    // Locked UI Palette
    public static final Color COLOR_PRIMARY = new Color(0x25, 0x63, 0xEB);       // #2563EB
    public static final Color COLOR_PRIMARY_HOVER = new Color(0x1D, 0x4E, 0xD8); // #1D4ED8
    public static final Color COLOR_BACKGROUND = new Color(0xF8, 0xFA, 0xFC);    // #F8FAFC
    public static final Color COLOR_SURFACE = new Color(0xFF, 0xFF, 0xFF);       // #FFFFFF
    public static final Color COLOR_SIDEBAR = new Color(0x0F, 0x17, 0x2A);       // #0F172A
    public static final Color COLOR_SIDEBAR_HOVER = new Color(0x1E, 0x29, 0x3B); // #1E293B
    public static final Color COLOR_SIDEBAR_TEXT = new Color(0x94, 0xA3, 0xB8);  // #94A3B8
    public static final Color COLOR_TEXT_MAIN = new Color(0x0F, 0x17, 0x2A);     // #0F172A
    public static final Color COLOR_TEXT_MUTED = new Color(0x64, 0x74, 0x8B);    // #64748B
    public static final Color COLOR_BORDER = new Color(0xE2, 0xE8, 0xF0);        // #E2E8F0
    public static final Color COLOR_SUCCESS = new Color(0x16, 0xA3, 0x4A);       // #16A34A
    public static final Color COLOR_WARNING = new Color(0xD9, 0x77, 0x06);       // #D97706
    public static final Color COLOR_DANGER = new Color(0xDC, 0x26, 0x26);        // #DC2626
    public static final Color COLOR_INFO = new Color(0x02, 0x84, 0xC7);          // #0284C7

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_CARD_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);
}
