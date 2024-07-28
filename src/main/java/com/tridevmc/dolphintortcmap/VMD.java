package com.tridevmc.dolphintortcmap;

//{
//    "VmdName": "GSModel",
//    "GenDomain": "SRAM",
//    "BigEndian": true,
//    "WordSize": 4,
//    "PointerSpacer": 1,
//    "Padding": 0,
//    "UsingRPC": false,
//    "AddSingles": [],
//    "RemoveSingles": [],
//    "AddRanges": [
//        [
//            982988,
//            993136
//        ],
//        [
//            993140,
//            996044
//        ],
//        [
//            996048,
//            997784
//        ],
//        [
//            997788,
//            999796
//        ],
//        [
//            999800,
//            1004224
//        ],
//        [
//            1004228,
//            1006492
//        ],
//        [
//            1006496,
//            1045852
//        ]
//    ],
//    "RemoveRanges": [],
//    "SuppliedBlastLayer": null
//}

// Annotate for GSON to serialize/deserialize with the correct field names.

import com.google.gson.annotations.SerializedName;

public record VMD(
        @SerializedName("VmdName") String vmdName,
        @SerializedName("GenDomain") String genDomain,
        @SerializedName("BigEndian") boolean bigEndian,
        @SerializedName("WordSize") int wordSize,
        @SerializedName("PointerSpacer") int pointerSpacer,
        @SerializedName("Padding") int padding,
        @SerializedName("UsingRPC") boolean usingRPC,
        @SerializedName("AddSingles") int[] addSingles,
        @SerializedName("RemoveSingles") int[] removeSingles,
        @SerializedName("AddRanges") int[][] addRanges,
        @SerializedName("RemoveRanges") int[][] removeRanges,
        @SerializedName("SuppliedBlastLayer") String suppliedBlastLayer
) {

    public static class Builder {
        private String vmdName;
        private int[][] addRanges;

        public Builder vmdName(String vmdName) {
            this.vmdName = vmdName;
            return this;
        }

        public Builder addRanges(int[][] addRanges) {
            this.addRanges = addRanges;
            return this;
        }

        public VMD build() {
            // Set the default values for the other fields.
            return new VMD(vmdName, "SRAM", true, 4, 1, 0, false, new int[0], new int[0], addRanges, new int[0][0], null);
        }
    }
}
