/*-
 * #%L
 * StrangeFX
 * %%
 * Copyright (C) 2020 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strange.simulator.local;

import org.redfx.strange.Gate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author johan
 */
@DisplayName("Test gate protocols")
public class GateProtocolTests {
    /*
    All those tests moved to Strange.
     */
    @Test
    public void ignore() {
        assertEquals(1,1);
    }
//    
//    @Test
//    @DisplayName("I gate")
//    public void unmarshalI() {
//        String s = "[[I]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 1);    
//        assertEquals(res[0].length, 1);
//        Gate expectI = res[0][0];
//        assertEquals(expectI, Gate.IDENTITY);
//    }   
//    
//    @Test
//    @DisplayName("IXH qubits")
//    public void unmarshalIXH() {
//        String s = "[[I,X,H]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 3);    
//        assertEquals(res[0].length, 1);
//        Gate expectI = res[0][0];
//        assertEquals(expectI, Gate.IDENTITY);
//        assertEquals(res[1][0], Gate.NOT);
//        assertEquals(res[2][0], Gate.HADAMARD);
//    }
//        
//    @Test
//    @DisplayName("IXH gates")
//    public void unmarshalIXHgates() {
//        String s = "[[I][X][H]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 1);    
//        assertEquals(res[0].length, 3);
//        Gate expectI = res[0][0];
//        assertEquals(expectI, Gate.IDENTITY);
//        assertEquals(res[0][1], Gate.NOT);
//        assertEquals(res[0][2], Gate.HADAMARD);
//    }    
//    
//    @Test
//    @DisplayName("IXH gates 2 qubits")
//    public void unmarshal3gates2qubits() {
//        String s = "[[I,H][X,S][H,I]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 2);    
//        assertEquals(res[0].length, 3);
//        assertEquals(res[0][0], Gate.IDENTITY);
//        assertEquals(res[0][1], Gate.NOT);
//        assertEquals(res[0][2], Gate.HADAMARD);
//        assertEquals(res[1][0], Gate.HADAMARD);
//        assertEquals(res[1][1], Gate.SWAP);
//        assertEquals(res[1][2], Gate.IDENTITY);
//    }
//    
//        
//    @Test
//    @DisplayName("CNOT12 2 qubits")
//    public void unmarshalCNOT12() {
//        String s = "[[C0,C]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 2);    
//        assertEquals(res[0].length, 1);
//        assertEquals(res[0][0], Gate.C0);
//        assertEquals(res[1][0], Gate.CNOT);
//    }
//            
//    @Test
//    @DisplayName("CNOT21 2 qubits")
//    public void unmarshalCNOT21() {
//        String s = "[[C,C0]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 2);    
//        assertEquals(res[0].length, 1);
//        assertEquals(res[0][0], Gate.CNOT);
//        assertEquals(res[1][0], Gate.C0);
//    }
//                
//    @Test
//    @DisplayName("SWAP 3 qubits")
//    public void unmarshalSWAP13() {
//        String s = "[[S,I,S]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 3);    
//        assertEquals(res[0].length, 1);
//        assertEquals(res[0][0], Gate.SWAP);
//        assertEquals(res[1][0], Gate.IDENTITY);
//        assertEquals(res[2][0], Gate.SWAP);
//    }
//    
//    @Test
//    @DisplayName("QFT")
//    public void unmarshalQFT2() {
//        String s= "[[QFT,QFT]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 2);    
//        assertEquals(res[0].length, 1);
//        assertEquals(res[0][0], Gate.QFT);
//        assertEquals(res[1][0], Gate.QFT);
//    }    
//    
//    @Test
//    @DisplayName("QFTX")
//    public void unmarshalQFT2X() {
//        String s= "[[QFT,QFT,X]]";
//        Gate[][] res = Gate.toMatrix(s);
//        assertEquals(res.length, 3);    
//        assertEquals(res[0].length, 1);
//        assertEquals(res[0][0], Gate.QFT);
//        assertEquals(res[1][0], Gate.QFT);
//        assertEquals(res[2][0], Gate.NOT);
//    }
//    
}
