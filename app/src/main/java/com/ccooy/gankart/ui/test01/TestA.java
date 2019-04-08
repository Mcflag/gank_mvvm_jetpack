package com.ccooy.gankart.ui.test01;

public class TestA {
    public static void main(String[] args) {
        int[] ins = {2, 3, 6, 1, 24, 5, 78, 43};
        int[] ins2 = sort2(ins);
        for (int in : ins2) {
            System.out.println(in);
        }
    }

    public static int[] sort(int[] ins) {
        for (int i = 1; i < ins.length; i++) {
            for (int j = i; j > 0; j--) {
                if (ins[j] > ins[j - 1]) {
                    int temp = ins[j];
                    ins[j] = ins[j - 1];
                    ins[j - 1] = temp;
                }
            }
        }
        return ins;
    }

    public static int[] sort2(int[] ins) {
        for (int i = 1; i < ins.length; i++) {
            int temp = ins[i];
            int j;
            for (j = i; j > 0 && ins[j - 1] > temp; j--) {
                ins[j] = ins[j - 1];
            }
            ins[j] = temp;
        }
        return ins;
    }
}
