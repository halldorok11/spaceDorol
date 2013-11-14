
public class TransformationMatrix {

	public static float[] getIdentityMatrix() {
		float[] M = new float[16];
		M[0] = 1;				M[4] = 0;				M[8]	= 0;	M[12]	= 0;
		M[1] = 0;				M[5] = 1;				M[9]	= 0;	M[13]	= 0;
		M[2] = 0;				M[6] = 0;				M[10]	= 1;	M[14]	= 0;
		M[3] = 0;				M[7] = 0;				M[11]	= 0;	M[15]	= 1;
		
		return M;
	}

	public static float[] multiply(float[] M, float[] M2) {
		float[] result = new float[16];

		// row 1
		result[0] = M[0]*M2[0] + M[4]*M2[1] + M[8]*M2[2] + M[12]*M2[3];
		result[4] = M[0]*M2[4] + M[4]*M2[5] + M[8]*M2[6] + M[12]*M2[7];
		result[8] = M[0]*M2[8] + M[4]*M2[9] + M[8]*M2[10] + M[12]*M2[11];
		result[12] = M[0]*M2[12] + M[4]*M2[13] + M[8]*M2[14] + M[12]*M2[15];

		//row 2
		result[1] = M[1]*M2[0] + M[5]*M2[1] + M[9]*M2[2] + M[13]*M2[3];
		result[5] = M[1]*M2[4] + M[5]*M2[5] + M[9]*M2[6] + M[13]*M2[7];
		result[9] = M[1]*M2[8] + M[5]*M2[9] + M[9]*M2[10] + M[13]*M2[11];
		result[13] = M[1]*M2[12] + M[5]*M2[13] + M[9]*M2[14] + M[13]*M2[15];

		//row 3
		result[2] = M[2]*M2[0] + M[6]*M2[1] + M[10]*M2[2] + M[14]*M2[3];
		result[6] = M[2]*M2[4] + M[6]*M2[5] + M[10]*M2[6] + M[14]*M2[7];
		result[10] = M[2]*M2[8] + M[6]*M2[9] + M[10]*M2[10] + M[14]*M2[11];
		result[14] = M[2]*M2[12] + M[6]*M2[13] + M[10]*M2[14] + M[14]*M2[15];

		//row 4
		result[3] = M[3]*M2[0] + M[7]*M2[1] + M[11]*M2[2] + M[15]*M2[3];
		result[7] = M[3]*M2[4] + M[7]*M2[5] + M[11]*M2[6] + M[15]*M2[7];
		result[11] = M[3]*M2[8] + M[7]*M2[9] + M[11]*M2[10] + M[15]*M2[11];
		result[15] = M[3]*M2[12] + M[7]*M2[13] + M[11]*M2[14] + M[15]*M2[15];

		return result;
	}
}
