package org.egibide;

import javax.net.ssl.SSLSocket;

public class HiloServidor extends Thread {

    private SSLSocket cliente;

    public HiloServidor(SSLSocket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        // Escribir....
    }
}
