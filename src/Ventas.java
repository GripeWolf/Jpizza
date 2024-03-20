import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ventas extends javax.swing.JPanel {

    Connection ccn;
    Statement st;
    ResultSet rs;
    double total = 0.0; // Variable para almacenar el total

    public Ventas() {
        initComponents();
        ccn = new Conexiones.Conexion().getConnection(); // Obtener la conexión desde la clase de conexión
        cargarProductosEnComboBox();
        enviarALaCocina.addActionListener((java.awt.event.ActionEvent evt) -> {
        enviarALaCocina();
    });
    }

    private void cargarProductosEnComboBox() {
        try {
            String sql = "SELECT Producto FROM Productos";
            st = ccn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                SeleccionDeProductos.addItem(rs.getString("Producto"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar productos: " + e);
        }
    }

    private void actualizarTotal() {
        total = 0.0; // Utilizar el campo total de la clase
        DefaultTableModel model = (DefaultTableModel) ProductosSeleccionados.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            double totalProducto = (double) model.getValueAt(i, 6); // Obtener el total del producto desde la columna 6
            total += totalProducto;
        }
        TOTAL.setText(String.valueOf(total));
    }

    private void agregarProductoSeleccionado() {
        String productoSeleccionado = SeleccionDeProductos.getSelectedItem().toString();
        String tipoProducto = obtenerTipoProducto(productoSeleccionado);
        double precioProducto = obtenerPrecio(productoSeleccionado);
        int cantidad = (int) Cantidad.getValue();
        double totalProducto = cantidad * precioProducto;

        String porciones = "";
        String horneado = "";

        // Verificar si el tipo de producto es "Bebidas"
        if (tipoProducto.equals("Bebidas")) {
            porciones = "."; // Establecer "NO" para bebidas
            horneado = "."; // Establecer "NO" para bebidas
        } else if (tipoProducto.equals("Pizzas")) {
            porciones = String.valueOf(Cantidad_Porciones.getSelectedItem());
            horneado = TipoHorneado.getSelectedItem().toString();

            // Calcular los cargos adicionales por tipo de horneado o tamaño de porciones
            double cargoAdicional = calcularCargosAdicionales(tipoProducto);
            totalProducto += cargoAdicional;
        }

        // Agregar el producto seleccionado a la tabla de productos seleccionados
        DefaultTableModel model = (DefaultTableModel) ProductosSeleccionados.getModel();
        model.addRow(new Object[]{productoSeleccionado, tipoProducto, porciones, horneado, cantidad, precioProducto, totalProducto});

        // Actualizar el total
        actualizarTotal();
    }

    private String obtenerTipoProducto(String producto) {
        String tipo = "";
        try {
            String sql = "SELECT Tipo FROM Productos WHERE Producto = ?";
            PreparedStatement pst = ccn.prepareStatement(sql);
            pst.setString(1, producto);
            rs = pst.executeQuery();
            if (rs.next()) {
                tipo = rs.getString("Tipo");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el tipo del producto: " + e);
        }
        return tipo;
    }

    private double obtenerPrecio(String producto) {
        double precio = 0.0;
        try {
            String sql = "SELECT Precio FROM Productos WHERE Producto = ?";
            PreparedStatement pst = ccn.prepareStatement(sql);
            pst.setString(1, producto);
            rs = pst.executeQuery();
            if (rs.next()) {
                precio = rs.getDouble("Precio");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el precio del producto: " + e);
        }
        return precio;
    }

    private double calcularCargosAdicionales(String tipoProducto) {
        double cargoAdicional = 0.0;
        if (tipoProducto.equals("Pizzas")) {
            // Obtener el precio adicional por tipo de horneado
            String tipoHorneado = TipoHorneado.getSelectedItem().toString();
            if (tipoHorneado.equals("A la Piedra")) {
                cargoAdicional += 50.0;
            } else if (tipoHorneado.equals("A la Parrilla")) {
                cargoAdicional += 100.0;
            } else if (tipoHorneado.equals("A la Leña")) {
                cargoAdicional += 150.0;
            } else if (tipoHorneado.equals("Normal")) {
                cargoAdicional += 0.0;
            }

            // Obtener el precio adicional por tamaño de porciones
            int cantidadPorciones = Integer.parseInt(Cantidad_Porciones.getSelectedItem().toString());
            cargoAdicional += (cantidadPorciones - 8) * 50.0; // Cada porción adicional tiene un costo de 50.0
        }
        return cargoAdicional;
    }

    private void enviarALaCocina() {
        try {
            // Obtener un nuevo número de factura único formateado
            String nuevoNumeroFactura = obtenerNuevoNumeroFacturaFormateado();

            String sql = "INSERT INTO Pedido (Productos, Tipo_Producto, Porciones, Tipo_Horneado, Cantidad, Nota_Pedido, Total_Pedido, Cliente, Telefono_Cliente, Pedido_Por, Delivery, Fecha_Hora, NumeroDeFactura) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
            PreparedStatement pst = ccn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            DefaultTableModel model = (DefaultTableModel) ProductosSeleccionados.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String producto = "";
                String tipoProducto = "";
                String porciones = "";
                String horneado = "";
                int cantidad = 0;

                // Obtener el índice de cada columna por nombre
                int productoColumnIndex = model.findColumn("PRODUCTO");
                int tipoProductoColumnIndex = model.findColumn("TIPO");
                int porcionesColumnIndex = model.findColumn("PORCIONES");
                int horneadoColumnIndex = model.findColumn("HORNEADO");
                int cantidadColumnIndex = model.findColumn("CANTIDAD");

                // Verificar si se encontraron todas las columnas
                if (productoColumnIndex != -1 && tipoProductoColumnIndex != -1 && porcionesColumnIndex != -1 && horneadoColumnIndex != -1 && cantidadColumnIndex != -1) {
                    // Obtener los valores de las columnas si se encontraron
                    producto = (String) model.getValueAt(i, productoColumnIndex);
                    tipoProducto = (String) model.getValueAt(i, tipoProductoColumnIndex);
                    porciones = (String) model.getValueAt(i, porcionesColumnIndex);
                    horneado = (String) model.getValueAt(i, horneadoColumnIndex);
                    cantidad = (int) model.getValueAt(i, cantidadColumnIndex);
                } else {
                    // Manejar el caso en el que no se encuentren todas las columnas
                    JOptionPane.showMessageDialog(null, "No se encontraron todas las columnas necesarias en el modelo de la tabla.");
                    return; // Salir del método si no se encuentran todas las columnas
                }

                // Configurar los parámetros para la consulta preparada
                pst.setString(1, producto);
                pst.setString(2, tipoProducto);
                pst.setString(3, porciones);
                pst.setString(4, horneado);
                pst.setInt(5, cantidad);
                pst.setString(6, NotaPedido.getText());
                pst.setDouble(7, Double.parseDouble(TOTAL.getText()));
                pst.setString(8, NomCliente.getText());
                pst.setString(9, Telefono.getText());
                pst.setString(10, PedidoPor.getSelectedItem().toString());
                pst.setString(11, Delivery.getSelectedItem().toString());
                pst.setString(12, nuevoNumeroFactura);

                pst.executeUpdate();

                // Obtener el valor autoincrementado del campo Numero_Pedido
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int numeroPedido = generatedKeys.getInt(1);
                    System.out.println("Numero de Pedido generado: " + numeroPedido);
                    // Aquí puedes hacer lo que necesites con el numeroPedido generado
                } else {
                    System.out.println("No se pudo obtener el número de pedido generado.");
                }
            }

            // Insertar el resumen del pedido en la tabla Cocina
            insertarResumenPedidoEnCocina(nuevoNumeroFactura);

            // Limpiar los campos después de enviar el pedido a la cocina
            limpiarCampos();

            JOptionPane.showMessageDialog(null, "Pedido enviado a la cocina correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al enviar el pedido a la cocina: " + e);
        }
    }

    private String obtenerNuevoNumeroFacturaFormateado() {
        // Obtener la fecha y hora actual
        LocalDateTime now = LocalDateTime.now();
        // Formatear la fecha y hora según el formato deseado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyyHHmmss");
        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }

    private void insertarResumenPedidoEnCocina(String nuevoNumeroFactura) {
        try {
            // Obtener el resumen del pedido
            String resumenPedido = obtenerResumenPedido();

            // Insertar el resumen del pedido en la tabla Cocina con el estado Pendiente
            String sql = "INSERT INTO Cocina (Resumen_Pedido, Estado) VALUES (?, ?)";
            PreparedStatement pst = ccn.prepareStatement(sql);
            pst.setString(1, resumenPedido);
            pst.setString(2, "Pendiente");
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar el resumen del pedido en la tabla Cocina: " + e);
        }
    }

    private String obtenerResumenPedido() {
        StringBuilder resumen = new StringBuilder();
        DefaultTableModel model = (DefaultTableModel) ProductosSeleccionados.getModel();
        resumen.append("Número de Factura: ").append(obtenerNuevoNumeroFacturaFormateado()).append("\n\n");
        resumen.append("Cliente: ").append(NomCliente.getText()).append("\n");
        resumen.append("Teléfono: ").append(Telefono.getText()).append("\n");
        resumen.append("Dirección: ").append(Direccion.getText()).append("\n");
        resumen.append("Sector: ").append(Sector.getText()).append("\n\n");
        resumen.append("Detalle del Pedido:\n");
        resumen.append("--------------------------------------------\n");
        for (int i = 0; i < model.getRowCount(); i++) {
            String producto = (String) model.getValueAt(i, 0);
            int cantidad = (int) model.getValueAt(i, 4);
            String porciones = (String) model.getValueAt(i, 2);
            String horneado = (String) model.getValueAt(i, 3);
            double totalProducto = (double) model.getValueAt(i, 6);
            resumen.append(producto).append(" ").append(porciones).append("\n").append(horneado).append(" x").append(cantidad).append(" -$").append(totalProducto).append("\n");
        }
        resumen.append("--------------------------------------------\n");
        resumen.append("Total: ").append(TOTAL.getText()).append("\n");
        resumen.append("--------------------------------------------\n");
        resumen.append("Delivery: ").append(Delivery.getSelectedItem().toString().equalsIgnoreCase("si") ? "Sí" : "No").append("\n");
        return resumen.toString();
    }

    private void limpiarCampos() {
        // Limpiar los campos de texto
        NomCliente.setText("");
        Telefono.setText("");
        Direccion.setText("");
        Sector.setText("");
        NotaPedido.setText("");

        // Restablecer la selección de los combobox
        SeleccionDeProductos.setSelectedIndex(0);
        PedidoPor.setSelectedIndex(0);
        Delivery.setSelectedIndex(0);
        TipoHorneado.setSelectedIndex(0);
        Cantidad_Porciones.setSelectedIndex(0);

        // Restablecer el valor de los spinner
        Cantidad.setValue(1);

        // Limpiar la tabla de productos seleccionados
        DefaultTableModel model = (DefaultTableModel) ProductosSeleccionados.getModel();
        model.setRowCount(0);

        // Restablecer el total a cero
        TOTAL.setText("0.0");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        Cantidad_Porciones = new javax.swing.JComboBox<>();
        Lporcion = new javax.swing.JLabel();
        TipoHorneado = new javax.swing.JComboBox<>();
        lHorneado = new javax.swing.JLabel();
        PedidoPor = new javax.swing.JComboBox<>();
        LpedidoP = new javax.swing.JLabel();
        NomCliente = new javax.swing.JTextField();
        LnombreC = new javax.swing.JLabel();
        Direccion = new javax.swing.JTextField();
        LDirC = new javax.swing.JLabel();
        Ltcliente = new javax.swing.JLabel();
        Telefono = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        NotaPedido = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        Sector = new javax.swing.JTextField();
        Lsector = new javax.swing.JLabel();
        Delivery = new javax.swing.JComboBox<>();
        LpedidoP1 = new javax.swing.JLabel();
        BtnAgregarProducto = new javax.swing.JButton();
        Lcantidad = new javax.swing.JLabel();
        Cantidad = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        ProductosSeleccionados = new javax.swing.JTable();
        enviarALaCocina = new javax.swing.JButton();
        QuitarProducto = new javax.swing.JButton();
        SeleccionDeProductos = new javax.swing.JComboBox<>();
        lHorneado1 = new javax.swing.JLabel();
        lto = new javax.swing.JLabel();
        TOTAL = new javax.swing.JTextField();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setForeground(new java.awt.Color(255, 255, 255));
        bg.setMinimumSize(new java.awt.Dimension(760, 630));
        bg.setPreferredSize(new java.awt.Dimension(760, 630));

        Cantidad_Porciones.setBackground(new java.awt.Color(102, 102, 102));
        Cantidad_Porciones.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Cantidad_Porciones.setForeground(new java.awt.Color(255, 255, 255));
        Cantidad_Porciones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8", "10", "12" }));
        Cantidad_Porciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cantidad_PorcionesActionPerformed(evt);
            }
        });

        Lporcion.setBackground(new java.awt.Color(0, 0, 0));
        Lporcion.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        Lporcion.setForeground(new java.awt.Color(0, 0, 0));
        Lporcion.setText("PORCIONES :");

        TipoHorneado.setBackground(new java.awt.Color(102, 102, 102));
        TipoHorneado.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        TipoHorneado.setForeground(new java.awt.Color(255, 255, 255));
        TipoHorneado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "A la Piedra", "A la Parrilla", "A la Leña" }));

        lHorneado.setBackground(new java.awt.Color(0, 0, 0));
        lHorneado.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        lHorneado.setForeground(new java.awt.Color(0, 0, 0));
        lHorneado.setText("HORNEADO :");

        PedidoPor.setBackground(new java.awt.Color(102, 102, 102));
        PedidoPor.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        PedidoPor.setForeground(new java.awt.Color(255, 255, 255));
        PedidoPor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mostrador", "Telefono", "Otro" }));

        LpedidoP.setBackground(new java.awt.Color(0, 0, 0));
        LpedidoP.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        LpedidoP.setForeground(new java.awt.Color(0, 0, 0));
        LpedidoP.setText("PEDIDO POR :");

        NomCliente.setBackground(new java.awt.Color(51, 51, 51));
        NomCliente.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        NomCliente.setForeground(new java.awt.Color(255, 255, 255));
        NomCliente.setText("N/A");

        LnombreC.setBackground(new java.awt.Color(0, 0, 0));
        LnombreC.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        LnombreC.setForeground(new java.awt.Color(0, 0, 0));
        LnombreC.setText("NOMBRE DEL CLIENTE ");

        Direccion.setBackground(new java.awt.Color(51, 51, 51));
        Direccion.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        Direccion.setForeground(new java.awt.Color(255, 255, 255));
        Direccion.setText("N/A");

        LDirC.setBackground(new java.awt.Color(0, 0, 0));
        LDirC.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        LDirC.setForeground(new java.awt.Color(0, 0, 0));
        LDirC.setText("DIRECCION");

        Ltcliente.setBackground(new java.awt.Color(0, 0, 0));
        Ltcliente.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Ltcliente.setForeground(new java.awt.Color(0, 0, 0));
        Ltcliente.setText("TELEFONO DEL CLIENTE");

        Telefono.setBackground(new java.awt.Color(51, 51, 51));
        Telefono.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        Telefono.setForeground(new java.awt.Color(255, 255, 255));
        Telefono.setText("8");
        Telefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TelefonoActionPerformed(evt);
            }
        });

        NotaPedido.setBackground(new java.awt.Color(51, 51, 51));
        NotaPedido.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        NotaPedido.setForeground(new java.awt.Color(255, 255, 255));
        NotaPedido.setText("N/A");
        jScrollPane3.setViewportView(NotaPedido);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("NOTA DEL PEDIDO :");

        Sector.setBackground(new java.awt.Color(51, 51, 51));
        Sector.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        Sector.setForeground(new java.awt.Color(255, 255, 255));
        Sector.setText("N/A");

        Lsector.setBackground(new java.awt.Color(0, 0, 0));
        Lsector.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Lsector.setForeground(new java.awt.Color(0, 0, 0));
        Lsector.setText("SECTOR ");

        Delivery.setBackground(new java.awt.Color(102, 102, 102));
        Delivery.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        Delivery.setForeground(new java.awt.Color(255, 255, 255));
        Delivery.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SI", "NO" }));

        LpedidoP1.setBackground(new java.awt.Color(0, 0, 0));
        LpedidoP1.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        LpedidoP1.setForeground(new java.awt.Color(0, 0, 0));
        LpedidoP1.setText(" DELIVERY :");

        BtnAgregarProducto.setFont(new java.awt.Font("Roboto Black", 1, 10)); // NOI18N
        BtnAgregarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Agregar.png"))); // NOI18N
        BtnAgregarProducto.setText("AÑADIR");
        BtnAgregarProducto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BtnAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAgregarProductoActionPerformed(evt);
            }
        });

        Lcantidad.setBackground(new java.awt.Color(0, 0, 0));
        Lcantidad.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Lcantidad.setForeground(new java.awt.Color(0, 0, 0));
        Lcantidad.setText("CANTIDAD");

        Cantidad.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N

        ProductosSeleccionados.setBackground(new java.awt.Color(51, 51, 51));
        ProductosSeleccionados.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        ProductosSeleccionados.setForeground(new java.awt.Color(255, 255, 255));
        ProductosSeleccionados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PRODUCTO", "TIPO", "PORCIONES", "HORNEADO", "CANTIDAD", "PRECIO", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ProductosSeleccionados.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane2.setViewportView(ProductosSeleccionados);

        enviarALaCocina.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        enviarALaCocina.setText("ENVIAR A LA COCINA");
        enviarALaCocina.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        QuitarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Eliminar.png"))); // NOI18N
        QuitarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QuitarProductoActionPerformed(evt);
            }
        });

        SeleccionDeProductos.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        SeleccionDeProductos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Productos" }));

        lHorneado1.setBackground(new java.awt.Color(0, 0, 0));
        lHorneado1.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        lHorneado1.setForeground(new java.awt.Color(0, 0, 0));
        lHorneado1.setText("PRODUCTOS :");

        lto.setBackground(new java.awt.Color(0, 0, 0));
        lto.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        lto.setForeground(new java.awt.Color(0, 0, 0));
        lto.setText("TOTAL :");

        TOTAL.setEditable(false);
        TOTAL.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LnombreC)
                    .addComponent(NomCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Ltcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Lsector)
                    .addComponent(Sector, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LDirC, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(156, 156, 156)
                .addComponent(QuitarProducto)
                .addGap(18, 18, 18)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(lto, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(TOTAL)
                        .addGap(18, 18, 18))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(enviarALaCocina)
                        .addContainerGap(20, Short.MAX_VALUE))))
            .addGroup(bgLayout.createSequentialGroup()
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Lcantidad)
                            .addComponent(Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(BtnAgregarProducto))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addComponent(Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SeleccionDeProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lHorneado1))
                        .addGap(6, 6, 6)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TipoHorneado, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lHorneado))
                        .addGap(6, 6, 6)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(bgLayout.createSequentialGroup()
                                .addComponent(Lporcion)
                                .addGap(69, 69, 69)
                                .addComponent(LpedidoP)
                                .addGap(65, 65, 65)
                                .addComponent(LpedidoP1))
                            .addGroup(bgLayout.createSequentialGroup()
                                .addComponent(Cantidad_Porciones, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(PedidoPor, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(Delivery, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 742, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgLayout.createSequentialGroup()
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lHorneado1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LpedidoP1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lHorneado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lporcion, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LpedidoP, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SeleccionDeProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TipoHorneado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Cantidad_Porciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PedidoPor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Delivery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(20, 20, 20)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(Lcantidad)
                        .addGap(2, 2, 2)
                        .addComponent(Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(BtnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(LnombreC)
                        .addGap(5, 5, 5)
                        .addComponent(NomCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Ltcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(bgLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5)
                        .addComponent(Lsector)
                        .addGap(5, 5, 5)
                        .addComponent(Sector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(LDirC, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(QuitarProducto)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lto)
                            .addComponent(TOTAL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(enviarALaCocina, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 760, 630));
    }// </editor-fold>//GEN-END:initComponents

    private void Cantidad_PorcionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cantidad_PorcionesActionPerformed
enviarALaCocina.addActionListener((java.awt.event.ActionEvent evt1) -> {
    enviarALaCocina();
});
    }//GEN-LAST:event_Cantidad_PorcionesActionPerformed

    private void BtnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAgregarProductoActionPerformed
        agregarProductoSeleccionado();
    }//GEN-LAST:event_BtnAgregarProductoActionPerformed

    private void QuitarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QuitarProductoActionPerformed
        DefaultTableModel model = (DefaultTableModel) ProductosSeleccionados.getModel();
        int selectedRow = ProductosSeleccionados.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
            
            actualizarTotal();
        }
    }//GEN-LAST:event_QuitarProductoActionPerformed

    private void TelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TelefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TelefonoActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnAgregarProducto;
    private javax.swing.JSpinner Cantidad;
    private javax.swing.JComboBox<String> Cantidad_Porciones;
    private javax.swing.JComboBox<String> Delivery;
    private javax.swing.JTextField Direccion;
    private javax.swing.JLabel LDirC;
    private javax.swing.JLabel Lcantidad;
    private javax.swing.JLabel LnombreC;
    private javax.swing.JLabel LpedidoP;
    private javax.swing.JLabel LpedidoP1;
    private javax.swing.JLabel Lporcion;
    private javax.swing.JLabel Lsector;
    private javax.swing.JLabel Ltcliente;
    private javax.swing.JTextField NomCliente;
    private javax.swing.JTextPane NotaPedido;
    private javax.swing.JComboBox<String> PedidoPor;
    private javax.swing.JTable ProductosSeleccionados;
    private javax.swing.JButton QuitarProducto;
    private javax.swing.JTextField Sector;
    private javax.swing.JComboBox<String> SeleccionDeProductos;
    private javax.swing.JTextField TOTAL;
    private javax.swing.JTextField Telefono;
    private javax.swing.JComboBox<String> TipoHorneado;
    private javax.swing.JPanel bg;
    private javax.swing.JButton enviarALaCocina;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lHorneado;
    private javax.swing.JLabel lHorneado1;
    private javax.swing.JLabel lto;
    // End of variables declaration//GEN-END:variables
}


