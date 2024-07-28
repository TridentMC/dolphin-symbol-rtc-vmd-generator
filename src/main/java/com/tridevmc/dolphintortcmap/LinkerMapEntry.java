package com.tridevmc.dolphintortcmap;

public record LinkerMapEntry(String address, String size, String vma, String name) {
    public int[] addressRange() {
        // Address, Size, VMA, 0, Name
        // 800065a8 00000020 800065a8 0 zz_00065a8_
        var address = Long.parseLong(this.address(), 16);
        var size = Long.parseLong(this.size(), 16);
        var offset = 0x80000000;
        return new int[]{(int) (address - offset), (int) (address + size - offset)};
    }
}
