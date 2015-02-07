Imports System.Windows.Forms


Public Class ServerForm


    Public board As Board
    Public WithEvents server As New MyServer()
    Private WithEvents timer As New Forms.Timer()

    Private gotColors As New List(Of Char)

    Private Sub ServerForm_Closed(sender As Object, e As System.EventArgs) Handles Me.Closed
        server.terminate()
    End Sub


    Private Sub ServerForm_Loaded(sender As Object, e As System.Windows.RoutedEventArgs) Handles Me.Loaded
        board = New Board(800)
    End Sub


    Private Sub bServer_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles bServer.Click
        If server.connected Then
            server.terminate()
            bServer.Content = "Server starten"
        Else
            tConsole.Text = ""
            server = New MyServer(tIP.Text, CInt(tPort.Text))
            server.start()
            bServer.Content = "Server beenden"
        End If
    End Sub

    Private Sub bGame_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles bGame.Click
        If bGame.Content = "Spiel starten" Then
            server.sendStart(board.size)
            bGame.Content = "Spiel beenden"
            timer.Interval = 1000
            timer.Enabled = True
            timer.Start()
        Else
            server.sendEnd()
            bGame.Content = "Spiel starten"
        End If
    End Sub




    Private Sub server_gotColors(nick As Integer, colors() As Char) Handles server.gotColors
        For i = 0 To 3

            Dim team As Boolean
            If colors(i) = "b" Then
                team = False
                board.players(nick + i) = New Player(board.size, team, board.random)
            ElseIf colors(i) = "r" Then
                team = True
                board.players(nick + i) = New Player(board.size, team, board.random)
            Else
                board.players(nick + i) = New Player(False)
            End If

            gotColors(nick + i) = colors(i)
        Next

        Dim allTrue As Boolean = True
        For i = 0 To gotColors.Count - 1
            If gotColors(i) = " " Then
                allTrue = False
                Exit For
            End If
        Next

        If allTrue Then
            server.sendGIDs(gotColors.ToArray())
        End If
    End Sub

    Private Sub server_gotKeys(nick As Integer, keys(,) As Boolean) Handles server.gotKeys
        For playerID = 0 To 3
            For keyID = 0 To 4
                board.players(nick + playerID).moveKeyDown(keyID) = keys(playerID, keyID)
            Next
        Next
    End Sub

    Private Sub server_newClient(connection As MyServer.Connection) Handles server.newClient
        board.addPlayer()
        board.players(board.players.Count - 1).usesKeys = False
        board.addPlayer()
        board.players(board.players.Count - 1).usesKeys = False
        board.addPlayer()
        board.players(board.players.Count - 1).usesKeys = False
        board.addPlayer()
        board.players(board.players.Count - 1).usesKeys = False

        gotColors.Add(" ")
        gotColors.Add(" ")
        gotColors.Add(" ")
        gotColors.Add(" ")
    End Sub


    Private Sub server_serverConsole(str As String) Handles server.serverConsole
        Me.Dispatcher.Invoke(New Action(Of String)(Sub(s) tConsole.Text &= s & vbCr), str.Replace(";", vbLf))
        'tConsole.Text &= str & ";"
    End Sub

    Private Sub server_wrongMessage(str As String) Handles server.wrongMessage
        Me.Dispatcher.Invoke(New Action(Of String)(Sub(s) tConsole.Text &= "-- Falsche Nachricht: -------------" & vbLf & s & vbLf & "-----------------------------------" & vbLf), str)
    End Sub

    Private Sub timer_Tick(sender As Object, e As System.EventArgs) Handles timer.Tick
        timer.Interval = 20
        board.move()


        'State senden
        Dim packages As New List(Of MyServer.PlayerPackage)
        Dim ballPos As Point = board.ball.position
        Dim ballSpeed As Vector = board.ball.speed
        Dim score() As Integer = board.scores

        Dim counter As Integer = 0
        For Each player In board.players
            Dim pack As New MyServer.PlayerPackage()
            pack.gid = counter
            pack.pos = player.position
            pack.speed = player.speed
            packages.Add(pack)
            counter += 1
        Next

        server.sendState(packages, ballPos, ballSpeed, score)
    End Sub
End Class
