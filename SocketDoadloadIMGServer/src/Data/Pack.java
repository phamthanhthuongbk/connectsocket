package Data;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import CompressData.CompressData;
import TEA.TEA;

public class Pack {
	public int id;
	public byte request;
	public int sizeData;
	public byte arrByte[];
	TEA tea;

	public Pack(InputStream inputStream) {


		tea = new TEA("And is there honey still for tea?".getBytes());
		
		
		receveRequest(inputStream);
		if (sizeData > 0)
			arrByte = receveData(inputStream);
		else
			arrByte = null;
	}

	
	private void receveRequest(InputStream inputStream) {
		
		DataInputStream dt = new DataInputStream(inputStream);
		try {
			request = dt.readByte();
			id = dt.readInt();
			sizeData = dt.readInt();
		} catch (IOException e1) {
			request = Request.CONNECT_ERROR;
			id = 0;
			sizeData = 0;
		}
	}

	private byte[] receveData(InputStream inputStream) {
		byte arr[] = new byte[sizeData];
		try {
			int r = 0;
			while (r != sizeData) {
				r += inputStream.read(arr, r, sizeData - r);
				System.out.println("Pack" +  "receve: " + r );
				if (r < 0) {
					System.out.println("Loi");
				}
			}
		} catch (IOException e) {
			System.out.println("Receive Data"+ "eroro");
		}
		
		//giai nen data
		byte[] compressData = null;
		try {
			compressData = CompressData.decompress(arr);
		} catch (IOException e) {
			System.out.println("Nen file bi loi");
		} catch (DataFormatException e) {
			System.out.println("Nen file bi loi");
			e.printStackTrace();
		}
		this.sizeData = compressData.length;
		
		//giai ma hoa
		byte[] teadata = tea.decrypt(compressData);
		this.sizeData = teadata.length;
		
		return teadata;
	}

	public Pack(int id, byte request, byte[] data) {

		tea = new TEA("And is there honey still for tea?".getBytes());
		
		
		this.id = id;
		this.request = request;
		if (data == null) {
			this.sizeData = 0;
			this.arrByte = null;
		} else {
			//ma hoa
			byte[] teadata = tea.encrypt(data);
			
			
			//nen data
			byte[] compressData = null;
			try {
				compressData = CompressData.compress(teadata);
			} catch (IOException e) {
				System.out.println("Nen file bi loi");
			}
			
			this.sizeData = compressData.length;
			this.arrByte = compressData;
		}
	}

	public byte[] toByteArray() {
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(a);
		try {
			out.writeByte(request); // 1
			out.writeInt(id); // 4
			out.writeInt(sizeData); // 4
			if (arrByte != null)
				out.write(arrByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return a.toByteArray();
	}

}
