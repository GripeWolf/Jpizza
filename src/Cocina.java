import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JOptionPane;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Cocina extends javax.swing.JPanel {

    Connection ccn;
    Statement st;
    ResultSet rs;

    public Cocina() {
        initComponents();
        ccn = new Conexiones.Conexion().getConnection();
        cargarPedidos();
        // Enlace del evento de clic del ratón en la tabla Pedidos
        Pedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PedidosMouseClicked(evt);
            }
        });
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgc = new javax.swing.JPanel();
        LpedidoL = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ResumenPedidoseleccionado = new javax.swing.JTextArea();
        ResPedido = new javax.swing.JLabel();
        Listo = new javax.swing.JButton();
        Imprimir = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        Pedidos = new javax.swing.JTable();
        Preparando = new javax.swing.JButton();

        bgc.setBackground(new java.awt.Color(255, 255, 255));
        bgc.setForeground(new java.awt.Color(255, 255, 255));
        bgc.setMinimumSize(new java.awt.Dimension(760, 630));

        LpedidoL.setBackground(new java.awt.Color(0, 0, 0));
        LpedidoL.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        LpedidoL.setForeground(new java.awt.Color(0, 0, 0));
        LpedidoL.setText("PEDIDOS PENDIENTES :");

        ResumenPedidoseleccionado.setEditable(false);
        ResumenPedidoseleccionado.setBackground(new java.awt.Color(51, 51, 51));
        ResumenPedidoseleccionado.setColumns(20);
        ResumenPedidoseleccionado.setForeground(new java.awt.Color(255, 255, 255));
        ResumenPedidoseleccionado.setRows(5);
        jScrollPane2.setViewportView(ResumenPedidoseleccionado);

        ResPedido.setBackground(new java.awt.Color(0, 0, 0));
        ResPedido.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        ResPedido.setForeground(new java.awt.Color(0, 0, 0));
        ResPedido.setText("RESUMEN DEL PEDIDO SELECCIONADO :");

        Listo.setBackground(new java.awt.Color(0, 102, 102));
        Listo.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Listo.setForeground(new java.awt.Color(0, 153, 51));
        Listo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/listo.png"))); // NOI18N
        Listo.setText("PEDIDO LISTO");
        Listo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ListoActionPerformed(evt);
            }
        });

        Imprimir.setBackground(new java.awt.Color(0, 0, 153));
        Imprimir.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Imprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/impresora.png"))); // NOI18N
        Imprimir.setText("IMPRIMIR FACTURA");
        Imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImprimirActionPerformed(evt);
            }
        });

        Pedidos.setBackground(new java.awt.Color(51, 51, 51));
        Pedidos.setFont(new java.awt.Font("Roboto Black", 0, 14)); // NOI18N
        Pedidos.setForeground(new java.awt.Color(255, 255, 255));
        Pedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PEDIDOS PENDIENTES", "ESTADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(Pedidos);

        Preparando.setBackground(new java.awt.Color(204, 102, 0));
        Preparando.setFont(new java.awt.Font("Roboto Black", 1, 12)); // NOI18N
        Preparando.setForeground(new java.awt.Color(255, 255, 255));
        Preparando.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Cocina.png"))); // NOI18N
        Preparando.setText("PREPARANDO");
        Preparando.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreparandoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bgcLayout = new javax.swing.GroupLayout(bgc);
        bgc.setLayout(bgcLayout);
        bgcLayout.setHorizontalGroup(
            bgcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgcLayout.createSequentialGroup()
                .addGroup(bgcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgcLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(LpedidoL)
                        .addGap(232, 232, 232)
                        .addComponent(ResPedido))
                    .addGroup(bgcLayout.createSequentialGroup()
                        .addGap(390, 390, 390)
                        .addComponent(Preparando, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgcLayout.createSequentialGroup()
                        .addGap(390, 390, 390)
                        .addComponent(Imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgcLayout.createSequentialGroup()
                        .addGap(390, 390, 390)
                        .addComponent(Listo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(bgcLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        bgcLayout.setVerticalGroup(
            bgcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgcLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(bgcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LpedidoL)
                    .addComponent(ResPedido))
                .addGap(5, 5, 5)
                .addGroup(bgcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(Preparando, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Listo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bgc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bgc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void cargarPedidos() {
        try {
            String sql = "SELECT ID_pedido, Estado FROM Cocina WHERE Estado IN ('Pendiente', 'Preparando')";
            st = ccn.createStatement();
            rs = st.executeQuery(sql);
            DefaultTableModel model = (DefaultTableModel) Pedidos.getModel();
            model.setRowCount(0); // Limpiar la tabla antes de cargar nuevos datos
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("ID_pedido"), rs.getString("Estado")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar pedidos: " + e);
        }
    }

    private void mostrarResumenPedido(int idPedido) {
        try {
            String sql = "SELECT Resumen_Pedido FROM Cocina WHERE ID_pedido = ?";
            PreparedStatement pst = ccn.prepareStatement(sql);
            pst.setInt(1, idPedido);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String resumenPedido = rs.getString("Resumen_Pedido");
                ResumenPedidoseleccionado.setText(resumenPedido);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar resumen del pedido: " + e);
        }
    }

    private void PedidosMouseClicked(java.awt.event.MouseEvent evt) {
        int filaSeleccionada = Pedidos.getSelectedRow();
        if (filaSeleccionada != -1) {
            int idPedido = (int) Pedidos.getValueAt(filaSeleccionada, 0); // Obtener el ID del pedido seleccionado
            mostrarResumenPedido(idPedido);
        }
    }

    private void actualizarEstadoPedido() {
        int filaSeleccionada = Pedidos.getSelectedRow();
        if (filaSeleccionada != -1) {
            int idPedido = (int) Pedidos.getValueAt(filaSeleccionada, 0); // Obtener el ID del pedido seleccionado
            try {
                String sql = "UPDATE Cocina SET Estado = 'Preparando' WHERE ID_pedido = ?";
                PreparedStatement pst = ccn.prepareStatement(sql);
                pst.setInt(1, idPedido);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Estado del pedido actualizado correctamente.");
                cargarPedidos(); // Actualizar la tabla de pedidos
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al actualizar el estado del pedido: " + e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione un pedido.");
        }
    }

   
    
    private void ImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImprimirActionPerformed
// Obtener el contenido del resumen del pedido
    String contenido = ResumenPedidoseleccionado.getText();
    
    // Lógica para imprimir el contenido en la impresora térmica
    try {
        // Crear un objeto PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();
        
        // Obtener la impresora predeterminada
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        
        // Establecer la impresora predeterminada
        job.setPrintService(service);
        
        // Crear un documento para imprimir
        Printable printable = new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                
                // Establecer la fuente y los márgenes
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
                
                // Dividir el contenido en líneas
                String[] lines = contenido.split("\n");
                
                // Imprimir cada línea
                int y = 20;
                for (String line : lines) {
                    g2d.drawString(line, 20, y);
                    y += 20;
                }
                
                return PAGE_EXISTS;
            }
        };
        
        // Asignar el documento para imprimir al trabajo de impresión
        job.setPrintable(printable);
        
        // Mostrar el diálogo de impresión y realizar la impresión
        if (job.printDialog()) {
            job.print();
        }
    } catch (PrinterException e) {
        JOptionPane.showMessageDialog(null, "Error al imprimir: " + e.getMessage());
    }
    }//GEN-LAST:event_ImprimirActionPerformed

    private void ListoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ListoActionPerformed
    int filaSeleccionada = Pedidos.getSelectedRow();
    if (filaSeleccionada != -1) {
        int idPedido = (int) Pedidos.getValueAt(filaSeleccionada, 0); // Obtener el ID del pedido seleccionado
        try {
            String sql = "UPDATE Cocina SET Estado = 'Despachado' WHERE ID_pedido = ?";
            PreparedStatement pst = ccn.prepareStatement(sql);
            pst.setInt(1, idPedido);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto marcado como Despachado.");
            cargarPedidos(); // Actualizar la tabla de pedidos
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al marcar el producto como Despachado: " + e);
        }
    } else {
        JOptionPane.showMessageDialog(null, "Por favor, seleccione un pedido.");
    }
    }//GEN-LAST:event_ListoActionPerformed

    private void PreparandoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreparandoActionPerformed
        actualizarEstadoPedido();
    }//GEN-LAST:event_PreparandoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Imprimir;
    private javax.swing.JButton Listo;
    private javax.swing.JLabel LpedidoL;
    private javax.swing.JTable Pedidos;
    private javax.swing.JButton Preparando;
    private javax.swing.JLabel ResPedido;
    private javax.swing.JTextArea ResumenPedidoseleccionado;
    private javax.swing.JPanel bgc;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
