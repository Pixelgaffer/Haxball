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
    Property port As Integer = 8001
    Property connected As Boolean = False

    Public Event serverConsole(s As String)
    Public Event newClient(connection As Connection)
    Public Event clientRemoved(connection As Connection)

    Public Event wrongMessage(s As String)
    Public Event gotColors(nick As Integer, colors() As Char)
    Public Event gotKeys(nick As Integer, keys(,) As Boolean)


    Structure Connection
        Public stream As NetworkStream
        Public streamw As StreamWriter
        Public streamr As StreamReader
        Public nick As Integer

        Public thread As System.Threading.Thread
    End Structure

    Structure PlayerPackage
        Public gid As Integer
        Public pos As Point
        Public speed As Vector
    End Structure


    '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''Invoke(New Action(Of String) (Sub(s) tConsole.Text &= s & VbCr), message)
    

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

                RaiseEvent serverConsole(s)

                evaluateMessage(c.nick, s)
            Catch ex As Exception
                'connections.Invoke(New Action(Of String)(Sub() connections.Remove(c)))
                Try
                    connections.Remove(c)
                    RaiseEvent clientRemoved(c)
                    RaiseEvent serverConsole(c.nick & " has exit.")
                    Exit While
                Catch exc As IndexOutOfRangeException
                    RaiseEvent serverConsole("Server geschlossen")
                    Exit While
                End Try
            End Try
        End While
    End Sub


    Sub evaluateMessage(nick As Integer, s As String)


        Try
            Dim firstSemikolon As Integer = s.IndexOf(";")
            Dim firstLine As String = s.Substring(0, s.IndexOf(";"))
            Dim rest As String = s.Substring(s.IndexOf(";") + 1)


            Select Case firstLine
                Case "colors"  ''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
                    Dim colors(3) As Char
                    For i = 0 To 3
                        colors(i) = rest.Substring(rest.IndexOf(" ") + 1, 1)
                        rest = rest.Substring(rest.IndexOf(";") + 1)
                    Next
                    RaiseEvent gotColors(nick, colors)
                Case "keys"  ''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
                    Dim keys(3, 4) As Boolean
                    For id = 0 To 3
                        rest = rest.Substring(rest.IndexOf(" ") + 1)
                        For keyID = 0 To 4
                            keys(id, keyID) = toBool(rest.Substring(keyID, 1))
                        Next
                    Next
                    RaiseEvent gotKeys(nick, keys)

                Case Else
                    RaiseEvent wrongMessage(s)
            End Select

        Catch ex As ArgumentOutOfRangeException
            RaiseEvent wrongMessage(s)
        End Try


    End Sub
    Private Function toBool(i As Integer)
        If i = 0 Then
            Return False
        Else
            Return True
        End If
    End Function





    Public Sub sendStart(size As Double)
        sendToAllClients("start" & ";" & "size=" & size)
    End Sub

    Public Sub sendGIDs(colors() As Char)
        Dim s As String = "gids"

        For i = 0 To colors.Length - 1
            s &= ";" & i & " " & colors(i)
        Next

        sendToAllClients(s)
    End Sub

    Public Sub sendState(packages As List(Of PlayerPackage), ballPos As Point, ballSpeed As Vector, score() As Integer)
        Dim s As String = "state"
        For Each pack In packages
            s &= ";" & pack.gid & " (" & pack.pos.X & ":" & pack.pos.Y & ") (" & pack.speed.X & ":" & pack.speed.Y & ")"
        Next
        s &= ";" & 100 & " (" & ballPos.X & ":" & ballPos.Y & ") (" & ballSpeed.X & ":" & ballSpeed.Y & ")"
        s &= ";" & score(0) & ":" & score(1)

        sendToAllClients(s)
    End Sub

    Sub sendEnd()
        sendToAllClients("end;")
    End Sub




    Public Sub sendToAllClients(ByVal s As String)
        For Each c In connections ' an alle clients weitersenden.
            Try
                c.streamw.WriteLine(s)
                c.streamw.Flush()
                RaiseEvent serverConsole("Server: " & s & vbLf)
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