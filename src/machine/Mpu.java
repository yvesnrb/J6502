package machine;

import java.io.FileInputStream;

import testing.VisibleForTesting;

public class Mpu {

    public Mpu(int resetVector) {
        this.programCounter = resetVector;
        this.processorFlags = 0b0000_0000;
        this.Acc            = 0x00;
        this.Xcc            = 0x00;
        this.Ycc            = 0x00;
        
        this.memStream = new int[3];
        this.addressStream = new int[3];
        this.memoryArray = new int[0x10000];
    }
    
//    private static int AM_ACCUMULATOR       = 0;
    private static int AM_ABSOLUTE          = 1;
    private static int AM_ABSOLUTE_X        = 2;
    private static int AM_ABSOLUTE_Y        = 3;
    private static int AM_IMMEDIATE         = 4;
//    private static int AM_IMPLIED           = 5;
//    private static int AM_INDIRECT          = 6;
    private static int AM_INDEXED_INDIRECT  = 7;
    private static int AM_INDIRECT_INDEXED  = 8;
//    private static int AM_REALATIVE         = 9;
    private static int AM_ZEROPAGE          = 10;
    private static int AM_ZEROPAGE_X        = 11;
//    private static int AM_ZEROPAGE_Y        = 12;
    
    public final int NEGATIVE_FLAG = 0;
    public final int OVERFLOW_FLAG = 1;
    public final int BREAK_FLAG    = 3;
    public final int DECIMAL_FLAG  = 4;
    public final int INTERUPT_FLAG = 5;
    public final int ZERO_FLAG     = 6;
    public final int CARRY_FLAG    = 7;
    
    private int[] memoryArray;
    private int Acc;
    private int Xcc;
    private int Ycc;
    private int processorFlags;
    private int programCounter;
    private int[] memStream;
    private int[] addressStream;
	private FileInputStream fin;
//  private int stackPointer;

    public int getAcc() {
        return Acc;
    }

    public int getXcc() {
        return Xcc;
    }

    public int getYcc() {
        return Ycc;
    }
    
    public int[] getMemoryArray() {
		return memoryArray;
	}
    
    @VisibleForTesting
	public void setMemStream(int[] memStream) {
		this.memStream = memStream;
	}
    @VisibleForTesting
	public void setAcc(int acc) {
		Acc = acc;
	}
    @VisibleForTesting
	public void setXcc(int xcc) {
		Xcc = xcc;
	}
    @VisibleForTesting
	public void setYcc(int ycc) {
		Ycc = ycc;
	}
    @VisibleForTesting
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	//Evaluates one opcode and increments the program counter accordingly
    public void step() {
        
        memStream[0] = memoryArray[this.programCounter];
        memStream[1] = memoryArray[this.programCounter + 1];
        memStream[2] = memoryArray[this.programCounter + 2];
        
        addressStream[0] = this.programCounter;
        addressStream[1] = this.programCounter + 1;
        addressStream[2] = this.programCounter + 2;
        
        evaluateOpCode(memStream[0]);
        
    }
    
    @VisibleForTesting
    public void evaluateOpCode(int opCode) {
    	switch(opCode) {
	        default:
	            programCounter += 1;
	            break;
	        //ADC immediate mode
	        case 0x69:
	            this.Acc += memoryArray[this.getOperandAddress(AM_IMMEDIATE)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            if (this.Acc > 0xFF) {
	            	this.Acc -= 0xFF;
	            	this.setFlags(CARRY_FLAG);
	            }
	            programCounter += 2;
	            break;
	        //ADC zeropage mode
	        case 0x65:
	            this.Acc += memoryArray[this.getOperandAddress(AM_ZEROPAGE)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            if (this.Acc > 0xFF) {
	            	this.Acc -= 0xFF;
	            	this.setFlags(CARRY_FLAG);
	            }
	            programCounter += 2;
	            break;
	        //ADC zeropage X-indexed
	        case 0x75:
	            this.Acc += memoryArray[this.getOperandAddress(AM_ZEROPAGE_X)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            programCounter += 2;
	            break;
	        //ADC absolute
	        case 0x6D:
	            this.Acc += memoryArray[this.getOperandAddress(AM_ABSOLUTE)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            programCounter += 3;
	            break;
	        //ADC absolute X-indexed
	        case 0x7D:
	            this.Acc += memoryArray[this.getOperandAddress(AM_ABSOLUTE_X)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            programCounter += 3;
	            break;
	        //ADC absolute Y-indexed
	        case 0x79:
	            this.Acc += memoryArray[this.getOperandAddress(AM_ABSOLUTE_Y)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            programCounter += 3;
	            break;
	        //ADC indirect indexed
	        case 0x61:
	            this.Acc += memoryArray[this.getOperandAddress(AM_INDIRECT_INDEXED)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            programCounter += 2;
	            break;
	        //ADC indexed indirect
	        case 0x71:
	            this.Acc += memoryArray[this.getOperandAddress(AM_INDEXED_INDIRECT)];
	            if (this.getFlags(this.CARRY_FLAG))
	                this.Acc += 1;
	            programCounter += 2;
	            break;
    	}
    }
    
    //Returns the address of an operand of an instruction, given the current program counter and the address mode
    private int getOperandAddress(int addressMode) {
        int indexedAddress;
        
        switch(addressMode) {
            default:
                return 0;
            //Absolute mode
            case 1:
                indexedAddress = memStream[1] + (memStream[2] * 0x100);
                return indexedAddress;
            //Absolute X-indexed mode
            case 2:
                indexedAddress = memStream[1] + (memStream[2] * 0x100);
                indexedAddress += this.Xcc;
                return indexedAddress;
            //Absolute Y-indexed mode
            case 3:
                indexedAddress = memStream[1] + (memStream[2] * 0x100);
                indexedAddress += this.Ycc;
                return indexedAddress;
            //Immediate mode
            case 4:
                indexedAddress = addressStream[1];
                return indexedAddress;
            //Indirect mode
            case 6:
                indexedAddress = memStream[1] + (memStream[2] * 0x100);
                indexedAddress = memoryArray[indexedAddress] + (memoryArray[indexedAddress + 1] * 0x100);
                return indexedAddress;
            //Indexed indirect mode   
            case 7:
                indexedAddress = memStream[1];
                indexedAddress += this.Xcc;
                if (indexedAddress >= 0x0100) {
                    indexedAddress -= 0x0100;
                }
                return indexedAddress;
            //Indirect Indexed mode
            case 8:
                indexedAddress = memoryArray[memStream[1]] + (memoryArray[memStream[1] + 1] * 0x100);
                indexedAddress += this.Ycc;
                return indexedAddress;
            //Zero-page mode
            case 10:
                indexedAddress = memStream[1] * 0x0100;
                return indexedAddress;
            //Zero-page X-indexed mode
            case 11:
                indexedAddress = memStream[1] * 0x100;
                indexedAddress += this.Xcc;
                return indexedAddress;
            //Zero page Y-indexed mode
            case 12:
                indexedAddress = memStream[1] * 0x100;
                indexedAddress += this.Ycc;
                return indexedAddress;
        }
    }
    
    public int[] getMemoryInterval(int startAddress, int endAddress) {
        int dumpSize;
        dumpSize = endAddress - startAddress + 1;
        int[] dumpArray;
        dumpArray = new int[dumpSize];
        int j = 0;
        for (int i = startAddress; i <= endAddress; i++) {
            dumpArray[j] = this.memoryArray[i];
            j++;
        }
        return dumpArray;
    }
    
    public int getMemoryAddress(int address) {
        return this.memoryArray[address];
    }
    
    public void setMemoryFromFile(String file) throws Exception {
        fin = new FileInputStream(file);
        int len;
        byte data[] = new byte[16];
        int i = 0;
        do {
            len = fin.read(data);
            for (int j = 0; j < len; j++) {
                this.memoryArray[i] = data[j] & 0xFF;
                i++;
            }
        } while (len != -1);
    }

    public boolean getFlags(int flag) {
        switch(flag) {
            case 0:
                if((this.processorFlags & 0b1000_0000) != 0)
                    return true;
                break;
            case 1:
                if((this.processorFlags & 0b0100_0000) != 0)
                    return true;
                break;
            case 3:
                if((this.processorFlags & 0b0001_0000) != 0)
                    return true;
                break;
            case 4:
                if((this.processorFlags & 0b0000_1000) != 0)
                    return true;
                break;
            case 5:
                if((this.processorFlags & 0b0000_0100) != 0)
                    return true;
                break;
            case 6:
                if((this.processorFlags & 0b0000_0010) != 0)
                    return true;
                break;
            case 7:
                if((this.processorFlags & 0b0000_0001) != 0)
                    return true;
                break;
            default:
                return false;
        }
        return false;
    }
    
    public boolean setFlags(int flag) {
        switch(flag) {
            case 0:
            	this.processorFlags |= 0b10000000;
            	break;
            case 1:
            	this.processorFlags |= 0b01000000;
                break;
            case 3:
            	this.processorFlags |= 0b00010000;
                break;
            case 4:
            	this.processorFlags |= 0b00001000;
                break;
            case 5:
            	this.processorFlags |= 0b00000100;
                break;
            case 6:
            	this.processorFlags |= 0b00000010;
                break;
            case 7:
            	this.processorFlags |= 0b00000001;
                break;
            default:
                return false;
        }
        return false;
    }
    
    public int getProgramCounter() {
        return programCounter;
    }
}
