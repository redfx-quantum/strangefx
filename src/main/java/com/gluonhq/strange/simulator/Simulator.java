/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gluonhq.strange.simulator;

import com.gluonhq.strange.Model;

/**
 *
 * @author johan
 */
public interface Simulator {
    
    public double[] calculateResults(Model m);
    
}
