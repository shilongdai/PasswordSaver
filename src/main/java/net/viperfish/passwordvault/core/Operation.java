/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.core;

import java.util.concurrent.Callable;

/**
 *
 * @author sdai
 */
public interface Operation<T> extends Callable<T> {
    public void undo();
}
