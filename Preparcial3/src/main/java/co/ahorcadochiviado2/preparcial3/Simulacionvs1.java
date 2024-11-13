package co.ahorcadochiviado2.preparcial3;

import java.util.concurrent.*;

class EstacionDeServicio {
    private final int capacidadTanque = 1000; // Capacidad máxima del tanque en galones
    private int tanqueActual = 0; // Cantidad actual en el tanque
    private final Semaphore mutex = new Semaphore(1); // Mutex para exclusión mutua
    private final Semaphore productorSemaphore; // Controlar el número de productores
    private final Semaphore clienteSemaphore; // Controlar los clientes esperando
    private final int ciclosMaximos; // Número máximo de ciclos de abastecimiento y consumo
    private int ciclosRealizados = 0; // Contador de ciclos

    public EstacionDeServicio(int numProductores, int ciclosMaximos) {
        this.productorSemaphore = new Semaphore(numProductores);
        this.clienteSemaphore = new Semaphore(0); // Al principio, los clientes no pueden cargar gasolina
        this.ciclosMaximos = ciclosMaximos;
    }

    public void abastecerGasolina(int galones) throws InterruptedException {
        productorSemaphore.acquire(); // Esperar que haya un productor disponible
        mutex.acquire(); // Asegurarse de tener acceso exclusivo al tanque

        // Si el tanque está lleno, el productor debe esperar
        while (tanqueActual + galones > capacidadTanque) {
            System.out.println("Tanque lleno. Esperando para abastecer.");
            mutex.release();
            Thread.sleep(500); // Esperar un poco
            mutex.acquire(); // Volver a intentar
        }

        tanqueActual += galones;
        System.out.println("Productor abasteció " + galones + " galones. Tanque actual: " + tanqueActual + " galones.");

        // Notificar a los clientes que pueden cargar gasolina si hay suficiente
        clienteSemaphore.release(); // Permitir a los clientes que puedan cargar gasolina

        mutex.release(); // Liberar el acceso al tanque
        productorSemaphore.release(); // Liberar el productor
    }

    public void cargarGasolina(int cantidad, String tipo) throws InterruptedException {
        clienteSemaphore.acquire(); // Esperar a que haya gasolina disponible para cargar
        mutex.acquire(); // Asegurarse de tener acceso exclusivo al tanque

        // Si el tanque está vacío, el cliente debe esperar
        while (tanqueActual < cantidad) {
            System.out.println("Tanque vacío. Esperando abastecimiento.");
            mutex.release();
            Thread.sleep(500); // Esperar un poco
            mutex.acquire(); // Volver a intentar
        }

        tanqueActual -= cantidad;
        System.out.println(tipo + " consumió " + cantidad + " galones. Tanque actual: " + tanqueActual + " galones.");

        // Liberar el mutex para permitir el acceso de otros clientes o productores
        mutex.release();
    }

    public void realizarCiclo() throws InterruptedException {
        if (ciclosRealizados >= ciclosMaximos) {
            System.out.println("Simulación terminada después de " + ciclosRealizados + " ciclos.");
            System.exit(0); // Terminar la simulación después de los ciclos máximos
        }
        ciclosRealizados++;
    }
}

class Productor implements Runnable {
    private EstacionDeServicio estacion;

    public Productor(EstacionDeServicio estacion) {
        this.estacion = estacion;
    }

    @Override
    public void run() {
        try {
            while (true) {
                estacion.abastecerGasolina(20); // Cada productor abastece 20 galones
                Thread.sleep(2000); // El productor tarda 2 segundos para cargar 20 galones
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Cliente implements Runnable {
    private EstacionDeServicio estacion;
    private String tipo; // "Vehículo" o "Motocicleta"
    private int consumo;

    public Cliente(EstacionDeServicio estacion, String tipo) {
        this.estacion = estacion;
        this.tipo = tipo;
        this.consumo = tipo.equals("Vehículo") ? 10 : 4; // Vehículo consume 10 galones, Motocicleta 4
    }

    @Override
    public void run() {
        try {
            while (true) {
                estacion.cargarGasolina(consumo, tipo);
                Thread.sleep(1500); // El cliente carga gasolina cada 1.5 segundos
                estacion.realizarCiclo(); // Realizar un ciclo de abastecimiento y consumo
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class SimulacionEstacionGasolina {
    public static void main(String[] args) {
        int numProductores = 4; // Número de productores (camiones cisterna)
        int ciclosMaximos = 35; // Ciclos máximos de simulación

        // Crear la estación de servicio
        EstacionDeServicio estacion = new EstacionDeServicio(numProductores, ciclosMaximos);

        // Crear hilos para los productores
        for (int i = 0; i < numProductores; i++) {
            new Thread(new Productor(estacion)).start();
        }

        // Crear hilos para los clientes (vehículos y motocicletas)
        new Thread(new Cliente(estacion, "Vehículo")).start();
        new Thread(new Cliente(estacion, "Motocicleta")).start();
    }
}
