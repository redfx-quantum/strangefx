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
package org.redfx.strangefx.simulator;

//import org.redfx.cloudlink.client.data.RemoteFunctionBuilder;
//import org.redfx.cloudlink.client.data.RemoteFunctionObject;
//import org.redfx.connect.GluonObservableObject;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 *
 * @author johan
 */
public class CloudSimulator {
    
//    public GluonObservableObject<String> calculateResults(String configuration) {
//        System.out.println("I will ask results for "+configuration+" from GCL");
//        RemoteFunctionObject function = RemoteFunctionBuilder.create("calculateResults").object();
//        function.setRawBody(configuration.getBytes());
//        GluonObservableObject<String> answer = function.call(String.class);
//        System.out.println("FUNCTION got answer: "+answer);
//        answer.stateProperty().addListener(new InvalidationListener() {
//            @Override
//            public void invalidated(Observable o) {
//                System.out.println("NEW ANSWER = "+answer.get());
//                System.out.println("NEW STATE = "+answer.getState());
//                if (answer.getException()!= null) {
//                    answer.getException().printStackTrace();
//                }
//            }
//        });
//        if (answer != null) {
//            System.out.println("answer = "+answer.get());
//        }
//        return answer;
//    }
}
