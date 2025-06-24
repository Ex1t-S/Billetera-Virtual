package Wallet;
import Controller.*;
import Wallet.*;
import Model.*;
import View.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
public class Wallet {
	 public static void main(String[] args) {
		 	new DataBaseManager();
	        new WalletController().startApp();
	   }
}
