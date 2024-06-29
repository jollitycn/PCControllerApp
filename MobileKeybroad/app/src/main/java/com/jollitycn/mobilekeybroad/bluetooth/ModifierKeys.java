package com.jollitycn.mobilekeybroad.bluetooth;

// 假设 bytes 是一个 byte 数组，并且已经初始化
public class ModifierKeys {  
    private byte[] bytes = new byte[3]; // 假设这个数组已经被初始化并包含有效数据
  
    // 定义位掩码常量  
    public static final byte CTRL_LEFT = 0b00000001; // 第0位  
    public static final byte SHIFT_LEFT = 0b00000010; // 第1位  
    public static final byte ALT_LEFT = 0b00000100; // 第2位  
    public static final byte GUI_LEFT = 0b00001000; // 第3位  
  
    public static final byte CTRL_RIGHT = 0b00010000; // 第4位  
    public static final byte SHIFT_RIGHT = 0b00100000; // 第5位  
    public static final byte ALT_RIGHT = 0b01000000; // 第6位  
    public static final byte GUI_RIGHT = (byte) 0b10000000; // 第7位
  
    // 构造器（如果需要的话）  
    public ModifierKeys() {
//        this.bytes = bytes;
    }

    public void setKey(byte value){getBytes()[2] = value;}
  
    // leftControl 的 getter 和 setter  
    public boolean isLeftControl() {  
        return (getBytes()[0] & CTRL_LEFT) != 0;
    }  
  
    public void setLeftControl(boolean value) {  
        getBytes()[0] = (byte) ((value ? getBytes()[0] | CTRL_LEFT : getBytes()[0] & ~CTRL_LEFT));
    }  
  
    // leftShift 的 getter 和 setter  
    public boolean isLeftShift() {  
        return (getBytes()[0] & SHIFT_LEFT) != 0;
    }  
  
    public void setLeftShift(boolean value) {  
        getBytes()[0] = (byte) ((value ? getBytes()[0] | SHIFT_LEFT : getBytes()[0] & ~SHIFT_LEFT));
    }
    // leftAlt 的 getter 和 setter
    public boolean isLeftAlt() {
        return (getBytes()[0] & ALT_LEFT) != 0;
    }

    public void setLeftAlt(boolean value) {
        getBytes()[0] = (byte) ((value ? getBytes()[0] | ALT_LEFT : getBytes()[0] & ~ALT_LEFT));
    }

    // leftGui 的 getter 和 setter
    public boolean isLeftGui() {
        return (getBytes()[0] & GUI_LEFT) != 0;
    }

    public void setLeftGui(boolean value) {
        getBytes()[0] = (byte) ((value ? getBytes()[0] | GUI_LEFT : getBytes()[0] & ~GUI_LEFT));
    }

    // rightControl 的 getter 和 setter
    public boolean isRightControl() {
        // 假设 rightControl 存储在 bytes[1] 的低四位（示例）
        return (getBytes()[1] & (CTRL_RIGHT & 0x0F)) != 0;
    }

    public void setRightControl(boolean value) {
        // 假设 rightControl 存储在 bytes[1] 的低四位（示例）
        getBytes()[1] = (byte) ((getBytes()[1] & ~(CTRL_RIGHT & 0x0F)) | (value ? (CTRL_RIGHT & 0x0F) : 0));
    }

    // rightShift 的 getter 和 setter
    public boolean isRightShift() {
        // 假设 rightShift 存储在 bytes[1] 的第5位（示例）
        return (getBytes()[1] & (SHIFT_RIGHT >> 4)) != 0;
    }

    public void setRightShift(boolean value) {
        // 假设 rightShift 存储在 bytes[1] 的第5位（示例）
        getBytes()[1] = (byte) ((getBytes()[1] & ~(SHIFT_RIGHT >> 4)) | (value ? (SHIFT_RIGHT >> 4) : 0));
    }

    // rightAlt 的 getter 和 setter
    public boolean isRightAlt() {
        // 假设 rightAlt 存储在 bytes[1] 的第6位（示例）
        return (getBytes()[1] & (ALT_RIGHT >> 4)) != 0;
    }

    public void setRightAlt(boolean value) {
        // 假设 rightAlt 存储在 bytes[1] 的第6位（示例）
        getBytes()[1] = (byte) ((getBytes()[1] & ~(ALT_RIGHT >> 4)) | (value ? (ALT_RIGHT >> 4) : 0));
    }

    // rightGui 的 getter 和 setter
    public boolean isRightGui() {
        // 假设 rightGui 存储在 bytes[1] 的最高位（示例）
        return (getBytes()[1] & (GUI_RIGHT >> 4)) != 0;
    }

    public void setRightGui(boolean value) {
        // 假设 rightGui 存储在 bytes[1] 的最高位（示例）
        getBytes()[1] = (byte) ((getBytes()[1] & ~(GUI_RIGHT >> 4)) | (value ? (GUI_RIGHT >> 4) : 0));
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void
      reset() {
        bytes = new byte[3];
    }


    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
//
//    public Byte getKey() {
//        return key;
//    }
//
//    public void setKey(Byte key) {
//        this.key = key;
//    }
//
//    // ... 类似地，为 leftAlt, leftGui, rightControl, rightShift, rightAlt, rightGui 定义 getter 和 setter ...
//
//    // 例如，rightGui 的 getter 和 setter
//    public boolean isRightGui() {
//        return (bytes[0] & GUI_RIGHT) != 0;
//    }
//
//    public void setRightGui(boolean value) {
//        bytes[0] = (byte) ((value ? bytes[0] | GUI_RIGHT : bytes[0] & ~GUI_RIGHT));
//    }
//
    // ... 其他代码 ...  
}