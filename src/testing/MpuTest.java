package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import machine.Mpu;

class MpuTest {
	private Mpu mpu = new Mpu(0x0000);
	
	@Before
	void setUp() {
		try {
			mpu.setMemoryFromFile("TestMemoryFile");
		} catch (Exception e) {
			fail("Memory file could not be opened");
		}
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
