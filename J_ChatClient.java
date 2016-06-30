//    ����TCP���������̡����ͻ��˳��򲿷֡�
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

public class J_ChatClient extends JFrame
{
    private ObjectInputStream m_input;   // ������
    private ObjectOutputStream m_output; // �����
    private JTextField m_enter;  // ��������
    private JTextArea m_display; // ��ʾ����

    public J_ChatClient( ) // ��ͼ�ν������������
    {
        super("�������ͻ���");
        Container c = getContentPane( );
        m_enter = new JTextField( );
        m_enter.setEnabled( false );
        m_enter.addActionListener(new ActionListener( )
            {
                public void actionPerformed( ActionEvent event )
                { // ��������˷�������
                    try
                    {
                        String s = event.getActionCommand( );
                        m_output.writeObject( s );
                        m_output.flush( );
                        mb_displayAppend( "�ͻ���: " + s );
                        m_enter.setText( "" ); // ������������ԭ������
                    }
                    catch (Exception e)
                    {
                        System.err.println("�����쳣:" + e);
                        e.printStackTrace( );
                    } // try-catch�ṹ����
                } // ����actionPerformed����
            } // ʵ�ֽӿ�ActionListener���ڲ������
        ); // addActionListener�������ý���
        c.add( m_enter, BorderLayout.NORTH );
        m_display = new JTextArea( );
        c.add( new JScrollPane( m_display ), BorderLayout.CENTER );
    } // J_ChatClient���췽������

    public void mb_displayAppend( String s )
    {
        m_display.append( s + "\n" );
        m_display.setCaretPosition( m_display.getText( ).length( ) );
        m_enter.requestFocusInWindow( ); // ת�����뽹�㵽��������
    } // ����mb_displayAppend����

    public boolean mb_isEndSession( String m )
    {
        if (m.equalsIgnoreCase("q"))
            return(true);
        if (m.equalsIgnoreCase("quit"))
            return(true);
        if (m.equalsIgnoreCase("exit"))
            return(true);
        if (m.equalsIgnoreCase("end"))
            return(true);
        if (m.equalsIgnoreCase("����"))
            return(true);
        return(false);
    } // ����mb_isEndSession����

    public void mb_run( String host, int port)
    {
        try
        {
            mb_displayAppend("��������");
            Socket s = new Socket(host, port);
            String m; // ���Է������˵���Ϣ
            m_output = new ObjectOutputStream( s.getOutputStream( ) );
            m_input = new ObjectInputStream( s.getInputStream( ) );
            m_enter.setEnabled( true );
            do
            {
                m = (String) m_input.readObject( );
                mb_displayAppend("��������: " + m);
            } while(!mb_isEndSession( m ));// do-whileѭ������
            m_output.writeObject("q"); // ֪ͨ���������˳�����
            m_output.flush( );
            m_output.close( );
            m_input.close( );
            s.close( );
            System.exit( 0 );
        }
        catch (Exception e)
        {
            System.err.println("�����쳣:" + e);
            e.printStackTrace( );
            mb_displayAppend("�����쳣");
        } // try-catch�ṹ����
    } // ����mb_run����

    public static void main(String args[ ])
    {
        J_ChatClient app = new J_ChatClient( );
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(350, 150);
        app.setVisible(true);
        if ( args.length == 0 )
            app.mb_run("localhost", 6000);
        else app.mb_run(args[0], 6000);
    } // ����main����
} // ��J_ChatClient����