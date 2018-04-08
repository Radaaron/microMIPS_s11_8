package microMIPS_s11_8;

public class Converter {
	
	public Converter() {}
	
	public String decimalToBinary(String in, int n) {
		// returns n bit binary with sign extension
		// parse in into base 10, convert to binary, then back to string
		int temp = Integer.parseInt(in, 10);
		in = Integer.toBinaryString(temp);
		// include leading zeroes
		if(in.length() < n) {
			String zeroes = "";
			for(int i = 0; i < (n - in.length()); i++) {
				zeroes = zeroes + "0";
			}
			in = zeroes + in;
		} 
		else if (in.length() > n) {
			int extra = in.length() - n;
			in = in.substring(extra, in.length());
		}
		return in;
	}
	
	public String hexToBinary(String in, int n) {
		// returns 16 bit binary with leading zeroes
		// parse in into base 16, convert to binary, then back to string
		int temp = Integer.parseInt(in, 16);
		in = Integer.toBinaryString(temp);
		// include leading zeroes
		if(in.length() < n) {
			String zeroes = "";
			for(int i = 0; i < (n - in.length()); i++) {
				zeroes = zeroes + "0";
			}
			in = zeroes + in;
		}
		return in;
	}
	
	public String binaryToHex(String in) {
		// returns 8 digit hex with leading zeroes
		// split into half since java doesnt parse 32 bit binary
		int mid = in.length() / 2;
		String[] parts = {in.substring(0, mid), in.substring(mid)};
		// parse in into base 2, convert to hex, then back to string
		for(int i = 0; i < parts.length; i++){
			int temp = Integer.parseInt(parts[i], 2);
			parts[i] = Integer.toHexString(temp);
			// include leading zeroes
			if(parts[i].length() < 4) {
				String zeroes = "";
				for(int j = 0; j < (4 - parts[i].length()); j++) {
					zeroes = zeroes + "0";
				}
				parts[i] = zeroes + parts[i];
			}
		}
		return(parts[0] + parts[1]).toUpperCase();
	}
}
