package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import machine.Mpu;

class MpuTest {
	private Mpu mpu = new Mpu(0x0000);
	private int[] memStream = new int[3];
	
	@BeforeEach
	void setUp() {
		mpu.resetFlags();
		mpu.setAcc(0x00);
		mpu.setXcc(0x01);
		mpu.setYcc(0x02);
		mpu.setProgramCounter(0x0000);
		this.memStream[0] = 0x00;
		this.memStream[1] = 0x01;
		this.memStream[2] = 0x01;
		mpu.setMemStream(memStream);
		try {
			mpu.setMemoryFromFile("TestMemoryFile");
		} catch (Exception e) {
			fail("Could not open test memory file");
		}
	}
	
	@Test
	void testADCImmediate() {
		//Test if instruction can add the next byte into Acc
		mpu.evaluateOpCode(0x69);
		assertEquals(0x01, mpu.getAcc());
		//Test if instruction will advance the program counter 2 bytes
		assertEquals(0x02, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x69);
		assertEquals(0x01, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x69);
		assertEquals(0x03, mpu.getAcc());
	}
	
	@Test
	void testADCZeroPage() {
		//Test if instruction can add memory address 0x0001 into Acc
		mpu.evaluateOpCode(0x65);
		assertEquals(0x02, mpu.getAcc());
		//Test if instruction will advance the program counter 2 bytes
		assertEquals(0x02, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x65);
		assertEquals(0x02, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x65);
		assertEquals(0x05, mpu.getAcc());
	}
	
	@Test
	void testADCZeroPageXIndexed() {
		//Test if instruction can add memory address 0x0001 (+Xcc) into Acc
		mpu.evaluateOpCode(0x75);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will advance the program counter 2 bytes
		assertEquals(0x02, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x75);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x75);
		assertEquals(0x07, mpu.getAcc());
	}
	
	@Test
	void testADCAbsolute() {
		//Test if instruction can add memory address 0x0101 into Acc
		mpu.evaluateOpCode(0x6D);
		assertEquals(0x02, mpu.getAcc());
		//Test if instruction will advance the program counter 3 bytes
		assertEquals(0x03, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x6D);
		assertEquals(0x02, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x6D);
		assertEquals(0x05, mpu.getAcc());
	}
	
	@Test
	void testADCAbsoluteXIndexed() {
		//Test if instruction can add memory address 0x0101 (+Xcc) into Acc
		mpu.evaluateOpCode(0x7D);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will advance the program counter 3 bytes
		assertEquals(0x03, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x7D);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x7D);
		assertEquals(0x07, mpu.getAcc());
	}
	
	@Test
	void testADCAbsoluteYIndexed() {
		//Test if instruction can add memory address 0x0101 (+Ycc) into Acc
		mpu.evaluateOpCode(0x79);
		assertEquals(0x04, mpu.getAcc());
		//Test if instruction will advance the program counter 3 bytes
		assertEquals(0x03, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x79);
		assertEquals(0x04, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x79);
		assertEquals(0x09, mpu.getAcc());
	}
	
	@Test
	void testADCIndirectX() {
		//Test if instruction can add memory address 0x0001 (+Xcc) into Acc
		mpu.evaluateOpCode(0x61);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will advance the program counter 2 bytes
		assertEquals(0x02, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x61);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x61);
		assertEquals(0x07, mpu.getAcc());
	}
	
	@Test
	void testADCIndirectY() {
		//Test if instruction can add memory address 0x0303 into Acc
		mpu.evaluateOpCode(0x71);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will advance the program counter 2 bytes
		assertEquals(0x02, mpu.getProgramCounter());
		//Test if instruction will loop around after Acc overflows
		mpu.setAcc(0xFF);
		mpu.evaluateOpCode(0x71);
		assertEquals(0x03, mpu.getAcc());
		//Test if instruction will set the carry flag when Acc overflows
		assertEquals(true, mpu.getFlags(mpu.CARRY_FLAG));
		//Test if instruction will add the 1 to Acc if the carry flag is set
		mpu.evaluateOpCode(0x71);
		assertEquals(0x07, mpu.getAcc());
	}

}
