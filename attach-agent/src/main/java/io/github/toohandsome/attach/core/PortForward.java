package io.github.toohandsome.attach.core;

/**
 * @author toohandsome
 */
public interface PortForward {

    void forward(int localPort  ,int remotePort);
    void forward(int localPort ,String remoteAddr ,int remotePort);
    void reset();
}
