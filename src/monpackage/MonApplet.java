package monpackage;

import javacard.framework.*;
import javacard.framework.Applet;
import javacard.framework.ISOException;

public class MonApplet extends Applet {

	/******************** Constantes ************************/ 
	public static final byte CLA_MONAPPLET= (byte) 0xB0; 
	public static final byte INS_INCREMENTER_COMPTEUR= 0x00; 
	public static final byte INS_DECREMENTER_COMPTEUR= 0x01; 
	public static final byte INS_INTERROGER_COMPTEUR= 0x02; 
	public static final byte INS_INITIALISER_COMPTEUR= 0x03;
	public static final short SW_VERIFICATION_FAILED = 0x6312;
	public static final short SW_PIN_VERIFICATION_REQUIRED = 0x6311;
	public static final short SW_INVALID_DEBIT = 0x6313;
	public static final short SW_INVALID_CREDIT = 0x6314;
    public static final byte VERIFIER_PIN =0x20;
    public static final byte DEBLOQUER_PIN=0x04;
	
	
	/******************Ctes pou la pin ***************/
	 //pin bloqué aprés 3 essaie consécutive
	public final static byte NB_LIMIT=0x03;
	 //la longueur de pin
	public final static byte LONGUEUR_PIN=0x04;
	
	/*********************** Variables ***************************/
	public static final short SEUIL=950;
	private short compteur;
	OwnerPIN pin;
	
	private MonApplet() {
		compteur= 200;
		pin=new OwnerPIN(NB_LIMIT,LONGUEUR_PIN);
		 //initialiser le code propre à l'applet
		byte []arr= {1,6,8,2};
		pin.update(arr,(short) 0, (byte)arr.length);
	}

	public static void install(byte bArray[], short bOffset, byte bLength)
			throws ISOException {
		new MonApplet().register();
	}

	public void process(APDU apdu) throws ISOException {
		// TODO Auto-generated method stub
		byte[] buffer= apdu.getBuffer();
		
		//si nous avons selectionner notre applet rien ne se passe ! c'est pour ca on a ajouté return !
		if(this.selectingApplet()){return ;}
		
		if(buffer[ISO7816.OFFSET_CLA]!=CLA_MONAPPLET) 
		   ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);

		switch(buffer[ISO7816.OFFSET_INS]){

		case INS_INCREMENTER_COMPTEUR:
			if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
			credit(apdu); 
		    break;
		case INS_DECREMENTER_COMPTEUR:
			if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
			debit(apdu); 
		    break;
		case INS_INTERROGER_COMPTEUR:
			if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
			checkAmount(apdu);
		   // buffer[0]=(byte) compteur; 
		    //apdu.setOutgoingAndSend((short)0, (short) 1); 
		    break;
		case INS_INITIALISER_COMPTEUR:
			if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		    apdu.setIncomingAndReceive(); 
		    compteur=buffer[ISO7816.OFFSET_CDATA]; 
		    break;
		case VERIFIER_PIN:
			verifierPin(apdu);
			break;
		case DEBLOQUER_PIN:
			pin.resetAndUnblock();
		default:

	}
	}
	public void verifierPin(APDU apdu) {
		byte []buffer=apdu.getBuffer();
		byte readBuffer=(byte) apdu.setIncomingAndReceive();

		// vérifier si le  pin est correct:
			if(pin.check(buffer,ISO7816.OFFSET_CDATA, readBuffer)==false || readBuffer != LONGUEUR_PIN) {
				buffer[0]=pin.getTriesRemaining();
				apdu.setOutgoingAndSend((short)0, (short) 1);
				ISOException.throwIt(SW_VERIFICATION_FAILED);
			}
			
			 
	}
	
	public void debit(APDU apdu) {
		byte []buffer=apdu.getBuffer();
		byte []data=new byte [3];
		System.arraycopy(buffer, ISO7816.OFFSET_CDATA, data, 0, 3);
		short debitamount=(short)(data[0]+(data[1]*10)+(data[2]*100));

		if(compteur-debitamount<0) {
			ISOException.throwIt(SW_INVALID_DEBIT);
		}
		compteur=(short) (compteur-debitamount);
	}
	public void checkAmount(APDU apdu) {
		byte []buffer=apdu.getBuffer();
		byte[] data= devideData();
		// Préparer la longueur de la réponse
        //apdu.setOutgoing();
        //apdu.setOutgoingLength((short) data.length);

        // Copier les données dans le buffer APDU
        Util.arrayCopyNonAtomic(data, (short) 0, buffer, (short) 0, (short) data.length);

        // Envoyer la réponse 
        apdu.setOutgoingAndSend((short)0, (short) data.length); 	
	}
	public byte[] devideData() {
		short tmp=compteur;
		short i=0;
		byte [] data=new byte[3];
		while(i!=3) {
			data[i]=(byte) (tmp%10);
			tmp=(short) (tmp/10);
			i++;
		}
		return data;
	}
	public void credit(APDU apdu) {
		byte[] buffer =apdu.getBuffer();
		byte []data=new byte [3];
		System.arraycopy(buffer, ISO7816.OFFSET_CDATA, data, 0, 3);
		short amount=(short)(data[0]+(data[1]*10)+(data[2]*100));
		if(amount+compteur>950) {
			ISOException.throwIt(SW_INVALID_CREDIT);
		}
		else {
			compteur+=amount;
		}
	}

}
