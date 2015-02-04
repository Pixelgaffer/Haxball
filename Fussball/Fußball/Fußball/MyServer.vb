Imports System.Net.Sockets
Imports System.IO
Imports System.Net

' TCP-MultiServer 
' C 2009 - Vincent Casser

Public Class MyServer


    Private server As TcpListener
    Private serverThread As System.Threading.Thread
    Private connections As New List(Of Connection)


    Property address As String = "0.0.0.0"
    Property port As Integer = 8000
    Property connected As Boolean = False

    Public Event serverConsole(s As String)
    Public Event newClient(connection As Connection)
    Public Event clientRemoved(connection As Connection)


    Structure Connection
        Public stream As NetworkStream
        Public streamw As StreamWriter
        Public streamr As StreamReader
        Public nick As Integer

        Public thread As System.Threading.Thread
    End Structure




    Sub New()
        serverThread = New System.Threading.Thread(AddressOf listenToConnection)
        serverThread.IsBackground = True
    End Sub

    Sub New(_address As String, _port As Integer)
        address = _address
        port = _port
        serverThread = New System.Threading.Thread(AddressOf listenToConnection)
        serverThread.IsBackground = True
    End Sub


    Public Function start() As Boolean
        If Not serverThread.IsAlive Then
            Try
                Dim ipendpoint As IPEndPoint = New IPEndPoint(System.Net.IPAddress.Parse(address), port)

                server = New TcpListener(ipendpoint)
                server.Start()
                connected = True
                RaiseEvent serverConsole("Server started on " & ipendpoint.Address.ToString & " : " & ipendpoint.Port)

                serverThread.Start()
            Catch ex As Exception
                Return False
            End Try
            Return True
        End If
        Return False
    End Function

    Public Sub terminate()
        If serverThread.IsAlive Then
            sendToAllClients("kicked")
            For Each c In connections
                c.stream = Nothing
                c.streamr = Nothing
                c.thread.Abort()
                RaiseEvent clientRemoved(c)
            Next
            connections = New List(Of Connection)

            server.Stop()  'bricht serverThread ab
            serverThread = New System.Threading.Thread(AddressOf listenToConnection)
            serverThread.IsBackground = True

            connected = False
            RaiseEvent serverConsole("Server terminated.")
        End If
    End Sub

    Public Sub kick(nick As String)
        sendToClient("kicked", nick)
        For Each c As Connection In connections
            If c.nick = nick Then
                c.streamr.Close()
                connections.Remove(c)
                RaiseEvent clientRemoved(c)
                Exit For
            End If
        Next

    End Sub


    Private Sub listenToConnection()
        While True
            Dim client As New TcpClient
            Try
                client = server.AcceptTcpClient  'auf neue Verbindung warten
            Catch ex As SocketException  'bei server.stop()
                Return
            End Try

            Dim c As New Connection
            c.stream = client.GetStream
            c.streamr = New StreamReader(c.stream)
            c.streamw = New StreamWriter(c.stream)
            c.streamr.ReadLine()  'hello auslesen
            
            c.nick = connections.Count
            c.thread = New System.Threading.Thread(AddressOf listenToMessage)
            c.thread.IsBackground = True

            connections.Add(c)
            RaiseEvent newClient(c)
            RaiseEvent serverConsole("Spieler " & c.nick & " has joined.")

            c.thread.Start(c)
        End While
    End Sub


    Private Sub listenToMessage(ByVal c As Connection)
        While True
            Try
                Dim s As String = ""
                s = c.streamr.ReadLine()  'auf Nachricht warten, Exception bricht ab(im besten Fall)


                


            Catch
                connections.Remove(c)
                RaiseEvent clientRemoved(c)
                RaiseEvent serverConsole(c.nick & " has exit.")
                Exit While
            End Try
        End While
    End Sub

    Public Sub sendToAllClients(ByVal s As String)
        For Each c In connections ' an alle clients weitersenden.
            Try
                c.streamw.WriteLine(s)
                c.streamw.Flush()
            Catch
                connections.Remove(c)
                RaiseEvent clientRemoved(c)
            End Try
        Next
    End Sub
    Public Sub sendToClient(ByVal s As String, ByVal nick As String)
        For Each c As Connection In connections
            If c.nick = nick Then
                Try
                    c.streamw.WriteLine(s)
                    c.streamw.Flush()
                Catch
                    connections.Remove(c)
                    RaiseEvent clientRemoved(c)
                End Try
            End If
        Next
    End Sub
    Public Sub sendToAllExceptOneClient(ByVal s As String, ByVal nick As String)
        For Each c As Connection In connections
            If Not c.nick = nick Then
                Try
                    c.streamw.WriteLine(s)
                    c.streamw.Flush()
                Catch
                    connections.Remove(c)
                    RaiseEvent clientRemoved(c)
                End Try
            End If
        Next
    End Sub

End Class