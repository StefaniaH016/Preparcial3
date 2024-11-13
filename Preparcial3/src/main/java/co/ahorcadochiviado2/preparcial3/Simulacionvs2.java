package co.ahorcadochiviado2.preparcial3;

class Estacion2DeServicio {
    private final int capacidadMaxima = 1000;  // Capacidad del tanque
    private int gasolinaDisponible = 0;  // Gasolina actual disponible
    private final Object lock = new Object();
    private final int ciclosMaximos; // Número máximo de ciclos de abastecimiento y consumo
    private int ciclosRealizados = 0;// Objeto de sincronización

    Estacion2DeServicio(int ciclosMaximos) {
        this.ciclosMaximos = ciclosMaximos;
    }

    // Método para que un productor abastezca gasolina
    public void abastecerGasolina(int cantidad) {
        synchronized (lock) {
            while (gasolinaDisponible + cantidad > capacidadMaxima) {
                try {
                    lock.wait();  // Espera hasta que haya espacio suficiente en el tanque
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            gasolinaDisponible += cantidad;
            System.out.println("Productor abastece " + cantidad + " galones. Gasolina disponible: " + gasolinaDisponible);
            lock.notifyAll();  // Notificar a los clientes y otros productores
        }
    }

    // Método para que un cliente consuma gasolina
    public void consumirGasolina(int cantidad) {
        synchronized (lock) {
            while (gasolinaDisponible < cantidad) {
                try {
                    lock.wait();  // Espera hasta que haya suficiente gasolina
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            gasolinaDisponible -= cantidad;
            System.out.println("Cliente consume " + cantidad + " galones. Gasolina disponible: " + gasolinaDisponible);
            lock.notifyAll();  // Notificar a los productores y otros clientes
        }
    }

    public void realizarCiclo() throws InterruptedException {
        if (ciclosRealizados >= ciclosMaximos) {
            System.out.println("Simulación terminada después de " + ciclosRealizados + " ciclos.");
            System.exit(0); // Terminar la simulación después de los ciclos máximos
        }
        ciclosRealizados++;
    }
}

class Productor2 implements Runnable {
    private final Estacion2DeServicio estacion;
    private final int id;

    public Productor2(Estacion2DeServicio estacion, int id) {
        this.estacion = estacion;
        this.id = id;
    }

    @Override
    public void run() {
        int cantidadAbastecida = 20;  // Cada productor abastece 20 galones por vez
        while (true) {
            estacion.abastecerGasolina(cantidadAbastecida);
            try {
                Thread.sleep(1000);  // Simula el tiempo de abastecimiento
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                estacion.realizarCiclo();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Cliente2 implements Runnable {
    private final Estacion2DeServicio estacion;
    private final String tipo;  // "vehiculo" o "motocicleta"

    public Cliente2(Estacion2DeServicio estacion, String tipo) {
        this.estacion = estacion;
        this.tipo = tipo;
    }

    @Override
    public void run() {
        int cantidadConsumida = tipo.equals("vehiculo") ? 10 : 4;  // Vehículo consume 10 galones, moto 4 galones
        while (true) {
            estacion.consumirGasolina(cantidadConsumida);
            try {
                Thread.sleep(2000);  // Simula el tiempo de consumo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class SimulacionEstacionDeServicio {
    public static void main(String[] args) {
        Estacion2DeServicio estacion = new Estacion2DeServicio(35);

        // Crear hilos de productores
        Thread productor1 = new Thread(new Productor2(estacion, 1));
        Thread productor2 = new Thread(new Productor2(estacion, 2));
        Thread productor3 = new Thread(new Productor2(estacion, 3));
        Thread productor4 = new Thread(new Productor2(estacion, 4));

        // Crear hilos de clientes
        Thread cliente1 = new Thread(new Cliente2(estacion, "vehiculo"));
        Thread cliente2 = new Thread(new Cliente2(estacion, "vehiculo"));
        Thread cliente3 = new Thread(new Cliente2(estacion, "motocicleta"));
        Thread cliente4 = new Thread(new Cliente2(estacion, "motocicleta"));

        // Iniciar hilos
        productor1.start();
        productor2.start();
        productor3.start();
        productor4.start();

        cliente1.start();
        cliente2.start();
        cliente3.start();
        cliente4.start();
    }
}
