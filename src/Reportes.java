import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Reportes extends javax.swing.JPanel {

    Connection ccn;
    PreparedStatement pst;
    ResultSet rs;

    public Reportes() {
        initComponents();
        ccn = new Conexiones.Conexion().getConnection(); // Obtener la conexión desde la clase de conexión
        mostrarVentas();
        mostrarPizzaMasVendida();
        mostrarVentasDelDia();
        configurarFechaChooser();
        configurarBotonBuscar();
    }

    private void mostrarVentas() {
        try {
            String sql = "SELECT ID, NumeroDeFactura, Fecha_Hora, Total_Pedido FROM Pedido";
            pst = ccn.prepareStatement(sql);
            rs = pst.executeQuery();

            // Limpiar la tabla antes de cargar nuevos datos
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            // Iterar sobre los resultados y agregarlos a la tabla
            while (rs.next()) {
                int id = rs.getInt("ID");
                String numeroFactura = rs.getString("NumeroDeFactura");
                String fecha = rs.getString("Fecha_Hora");
                double totalPedido = rs.getDouble("Total_Pedido");

                model.addRow(new Object[]{id, numeroFactura, fecha, totalPedido});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar las ventas: " + e);
        }
    }

    private void mostrarPizzaMasVendida() {
        try {
            String sql = "SELECT Productos, COUNT(*) AS cantidad FROM Pedido WHERE Tipo_Producto = 'Pizzas' GROUP BY Productos ORDER BY cantidad DESC LIMIT 1";
            pst = ccn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                String pizzaMasVendida = rs.getString("Productos");
                PizzaMasVendida.setText(pizzaMasVendida);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar la pizza más vendida: " + e);
        }
    }

    private void mostrarVentasDelDia() {
        try {
            // Obtener la fecha actual
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaActual = sdf.format(cal.getTime());

            String sql = "SELECT SUM(Total_Pedido) AS total FROM Pedido WHERE Fecha_Hora >= ? AND Fecha_Hora < DATE_ADD(?, INTERVAL 1 DAY)";
            pst = ccn.prepareStatement(sql);
            pst.setString(1, fechaActual);
            pst.setString(2, fechaActual);
            rs = pst.executeQuery();

            if (rs.next()) {
                double totalVentasDia = rs.getDouble("total");
                Ventasdeldia.setText(String.valueOf(totalVentasDia));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar las ventas del día: " + e);
        }
    }

    private void configurarFechaChooser() {
        // Agregar un evento de cambio de fecha al JDateChooser Fecha
        Fecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("date")) {
                    // Obtener la fecha seleccionada en Fecha
                    java.util.Date selectedDate = Fecha.getDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaSeleccionada = sdf.format(selectedDate);

                    // Actualizar las ventas del día según la fecha seleccionada
                    actualizarVentasDeFecha(fechaSeleccionada);
                }
            }
        });
    }

    private void actualizarVentasDeFecha(String fecha) {
        try {
            String sql = "SELECT SUM(Total_Pedido) AS total FROM Pedido WHERE Fecha_Hora >= ? AND Fecha_Hora < DATE_ADD(?, INTERVAL 1 DAY)";
            pst = ccn.prepareStatement(sql);
            pst.setString(1, fecha);
            pst.setString(2, fecha);
            rs = pst.executeQuery();

            if (rs.next()) {
                double totalVentasFecha = rs.getDouble("total");
                Ventasdefecha.setText(String.valueOf(totalVentasFecha));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar las ventas de la fecha seleccionada: " + e);
        }
    }

    private void actualizarVentasDeFechaBporF(String fecha) {
        try {
            String sql = "SELECT SUM(Total_Pedido) AS total FROM Pedido WHERE Fecha_Hora >= ? AND Fecha_Hora < DATE_ADD(?, INTERVAL 1 DAY)";
            pst = ccn.prepareStatement(sql);
            pst.setString(1, fecha);
            pst.setString(2, fecha);
            rs = pst.executeQuery();

            if (rs.next()) {
                double totalVentasFecha = rs.getDouble("total");
                lBporf.setText(String.valueOf(totalVentasFecha));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar las ventas de la fecha seleccionada en BporF: " + e);
        }
    }

    private void configurarBotonBuscar() {
        // Agregar ActionListener al botón de búsqueda
        Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarPorFactura();
            }
        });
    }

    private void buscarPorFactura() {
        String numeroFactura = BFactura.getText().trim();
        if (!numeroFactura.isEmpty()) {
            try {
                String sql = "SELECT ID, NumeroDeFactura, Fecha_Hora, Total_Pedido FROM Pedido WHERE NumeroDeFactura LIKE ?";
                pst = ccn.prepareStatement(sql);
                pst.setString(1, "%" + numeroFactura + "%");
                rs = pst.executeQuery();

                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0);

                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String numFactura = rs.getString("NumeroDeFactura");
                    String fecha = rs.getString("Fecha_Hora");
                    double totalPedido = rs.getDouble("Total_Pedido");

                    model.addRow(new Object[]{id, numFactura, fecha, totalPedido});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al buscar por factura: " + e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese el número de factura a buscar");
        }
    }

    private void eliminarPedido() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            int idPedido = (int) model.getValueAt(selectedRow, 0);
            try {
                String sql = "DELETE FROM Pedido WHERE ID = ?";
                pst = ccn.prepareStatement(sql);
                pst.setInt(1, idPedido);
                int result = pst.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Pedido eliminado correctamente");
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo eliminar el pedido");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar el pedido: " + e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un pedido para eliminar");
        }
    }



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        PV = new javax.swing.JLabel();
        PV1 = new javax.swing.JLabel();
        VF = new javax.swing.JLabel();
        BFactura = new javax.swing.JTextField();
        PV3 = new javax.swing.JLabel();
        PizzaMasVendida = new javax.swing.JLabel();
        Ventasdeldia = new javax.swing.JLabel();
        Ventasdefecha = new javax.swing.JLabel();
        EliminarPedido = new javax.swing.JButton();
        Buscar = new javax.swing.JButton();
        Fecha = new com.toedter.calendar.JDateChooser();
        lBporf = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jTable1.setBackground(new java.awt.Color(51, 51, 51));
        jTable1.setFont(new java.awt.Font("Roboto Black", 1, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID PEDIDO", "NUMERO DE FACTURA", "FECHA", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        PV.setBackground(new java.awt.Color(0, 0, 0));
        PV.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        PV.setForeground(new java.awt.Color(0, 0, 0));
        PV.setText("PIZZA MAS VENDIDA :");

        PV1.setBackground(new java.awt.Color(0, 0, 0));
        PV1.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        PV1.setForeground(new java.awt.Color(0, 0, 0));
        PV1.setText("BUSCAR FACTURA NO. :");

        VF.setBackground(new java.awt.Color(0, 0, 0));
        VF.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        VF.setForeground(new java.awt.Color(0, 0, 0));
        VF.setText("VENTA DE LA FECHA :");

        PV3.setBackground(new java.awt.Color(0, 0, 0));
        PV3.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        PV3.setForeground(new java.awt.Color(0, 0, 0));
        PV3.setText("VENTAS DEL DIA :");

        PizzaMasVendida.setBackground(new java.awt.Color(0, 0, 0));
        PizzaMasVendida.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        PizzaMasVendida.setForeground(new java.awt.Color(0, 0, 0));

        Ventasdeldia.setBackground(new java.awt.Color(0, 0, 0));
        Ventasdeldia.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Ventasdeldia.setForeground(new java.awt.Color(0, 0, 0));

        Ventasdefecha.setBackground(new java.awt.Color(0, 0, 0));
        Ventasdefecha.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Ventasdefecha.setForeground(new java.awt.Color(0, 0, 0));

        EliminarPedido.setBackground(new java.awt.Color(51, 51, 51));
        EliminarPedido.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        EliminarPedido.setForeground(new java.awt.Color(255, 255, 255));
        EliminarPedido.setText("ELIMINAR PEDIDO");
        EliminarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarPedidoActionPerformed(evt);
            }
        });

        Buscar.setBackground(new java.awt.Color(51, 51, 51));
        Buscar.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Buscar.setForeground(new java.awt.Color(255, 255, 255));
        Buscar.setText("BUSCAR");

        lBporf.setBackground(new java.awt.Color(0, 0, 0));
        lBporf.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        lBporf.setForeground(new java.awt.Color(0, 0, 0));
        lBporf.setText("VENTA DE LA FECHA :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(VF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Ventasdefecha, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(EliminarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(PV)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(PizzaMasVendida, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(PV3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Ventasdeldia, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lBporf)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(PV1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32)))
                .addGap(2, 2, 2))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PV)
                    .addComponent(PizzaMasVendida))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PV3)
                    .addComponent(Ventasdeldia))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VF)
                    .addComponent(Ventasdefecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lBporf)
                        .addGap(7, 7, 7)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PV1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EliminarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Fecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(61, 61, 61))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void EliminarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarPedidoActionPerformed
    // Obtener el índice de la fila seleccionada en la tabla
    int filaSeleccionada = jTable1.getSelectedRow();
    
    // Verificar si se ha seleccionado una fila
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione un pedido para eliminar.");
        return;
    }
    
    // Obtener el ID del pedido de la fila seleccionada
    int idPedido = (int) jTable1.getValueAt(filaSeleccionada, 0);
    
    // Confirmar si realmente desea eliminar el pedido
    int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar este pedido?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
    if (confirmacion == JOptionPane.YES_OPTION) {
        try {
            // Ejecutar la consulta SQL para eliminar el pedido
            String sql = "DELETE FROM Pedido WHERE ID = ?";
            pst = ccn.prepareStatement(sql);
            pst.setInt(1, idPedido);
            pst.executeUpdate();
            
            // Actualizar la tabla
            mostrarVentas();
            JOptionPane.showMessageDialog(null, "Pedido eliminado correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el pedido: " + e);
        }
    }
    }//GEN-LAST:event_EliminarPedidoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BFactura;
    private javax.swing.JButton Buscar;
    private javax.swing.JButton EliminarPedido;
    private com.toedter.calendar.JDateChooser Fecha;
    private javax.swing.JLabel PV;
    private javax.swing.JLabel PV1;
    private javax.swing.JLabel PV3;
    private javax.swing.JLabel PizzaMasVendida;
    private javax.swing.JLabel VF;
    private javax.swing.JLabel Ventasdefecha;
    private javax.swing.JLabel Ventasdeldia;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lBporf;
    // End of variables declaration//GEN-END:variables
}
