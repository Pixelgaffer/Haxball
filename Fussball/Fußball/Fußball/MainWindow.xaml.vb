Imports System.Windows.Forms

Class MainWindow

    Public board As New Board()
    Public clientBoard As New Board()

    Property serverMode As Boolean
        Get
            If bMode.Content = "zum Server-Modus" Then
                Return False
            Else
                Return True
            End If
        End Get
        Set(value As Boolean)
            If value Then
                bMode.Content = "zum Lokal-Modus"
            Else
                bMode.Content = "zum Server-Modus"
            End If
        End Set
    End Property
    Public WithEvents client As New Client("localhost", 8001)
    Private WithEvents timer As New Forms.Timer()
    Public serverForm As New ServerForm()

    Private Declare Function GetAsyncKeyState Lib "user32" Alias "GetAsyncKeyState" (ByVal vKey As Keys) As Int16
    Private Function IsKeyPressed(ByVal vKey As Keys) As Boolean
        Return (GetAsyncKeyState(vKey) And &H8000) = &H8000
    End Function




    Private Sub MainWindow_Loaded(sender As Object, e As System.Windows.RoutedEventArgs) Handles Me.Loaded
        board.addPlayer()
        board.addPlayer()
        board.addPlayer()
        board.addPlayer()
        timer.Interval = 20
        timer.Start()
        flyout.IsOpen = True


        board.players(0).keyDown = Keys.S
        board.players(0).keyUp = Keys.W
        board.players(0).keyLeft = Keys.A
        board.players(0).keyRight = Keys.D
        board.players(0).keyShoot = Keys.Space

        board.players(1).keyDown = Keys.Down
        board.players(1).keyUp = Keys.Up
        board.players(1).keyLeft = Keys.Left
        board.players(1).keyRight = Keys.Right
        board.players(1).keyShoot = Keys.NumPad0

        board.players(2).keyDown = Keys.J
        board.players(2).keyUp = Keys.U
        board.players(2).keyLeft = Keys.H
        board.players(2).keyRight = Keys.K
        board.players(2).keyShoot = Keys.B

        board.players(3).keyDown = Keys.NumPad5
        board.players(3).keyUp = Keys.NumPad8
        board.players(3).keyLeft = Keys.NumPad4
        board.players(3).keyRight = Keys.NumPad6
        board.players(3).keyShoot = Keys.Enter

    End Sub



    Sub draw()
        If cViewer IsNot Nothing Then

            cViewer.Children.Clear()

            Dim elements As List(Of UIElement)
            If serverMode Then
                elements = clientBoard.draw()
            Else
                elements = board.draw()
            End If

            For Each element In elements
                If TypeOf (element) Is Shape Then
                    CType(element, Shape).Stroke = Brushes.Black
                End If

                cViewer.Children.Add(element)
            Next


        End If
    End Sub





    Private Sub timer_Tick(sender As Object, e As System.EventArgs) Handles Timer.Tick
        draw()

        Dim pressedKeys(3, 4) As Boolean
        For i = 0 To 3
            If i < board.players.Count Then
                pressedKeys(i, 0) = IsKeyPressed(board.players(i).keyUp)
                pressedKeys(i, 1) = IsKeyPressed(board.players(i).keyLeft)
                pressedKeys(i, 2) = IsKeyPressed(board.players(i).keyDown)
                pressedKeys(i, 3) = IsKeyPressed(board.players(i).keyRight)
                pressedKeys(i, 4) = IsKeyPressed(board.players(i).keyShoot)
            Else
                pressedKeys(i, 0) = False
                pressedKeys(i, 1) = False
                pressedKeys(i, 2) = False
                pressedKeys(i, 3) = False
                pressedKeys(i, 4) = False
            End If
        Next
        client.sendKeyUpdate(pressedKeys)
    End Sub










    Private Sub client_connectionFailed() Handles client.connectionFailed
        Console.Write("Connection Failed")
    End Sub
    Private Sub client_wrongMessage(s As String) Handles client.wrongMessage
        Console.WriteLine("Falsche Nachricht:" & vbLf & s)
    End Sub
    Private Sub client_notConnected() Handles client.notConnected
        Console.WriteLine("Der Client ist noch nicht verbunden")
    End Sub
    Private Sub client_finish() Handles client.finish
        Console.WriteLine("Spiel beendet")
    End Sub


    Private Sub client_start(size As Double) Handles client.start
        clientBoard = New Board(size)
        Dim colors(3) As Char

        If checkPlayer0.IsChecked = False Then
            colors(0) = "n"
        ElseIf colorPlayer0.SelectedIndex = 0 Then
            colors(0) = "r"
        Else
            colors(0) = "b"
        End If
        If checkPlayer1.IsChecked = False Then
            colors(1) = "n"
        ElseIf colorPlayer1.SelectedIndex = 0 Then
            colors(1) = "r"
        Else
            colors(1) = "b"
        End If
        If checkPlayer2.IsChecked = False Then
            colors(2) = "n"
        ElseIf colorPlayer2.SelectedIndex = 0 Then
            colors(2) = "r"
        Else
            colors(2) = "b"
        End If
        If checkPlayer3.IsChecked = False Then
            colors(3) = "n"
        ElseIf colorPlayer3.SelectedIndex = 0 Then
            colors(3) = "r"
        Else
            colors(3) = "b"
        End If
        client.sendColors(colors)
    End Sub

    Private Sub client_playerGIDList(list As System.Collections.Generic.List(Of Client.PlayerGID)) Handles client.playerGIDList
        For Each gid In list
            clientBoard.addPlayer(gid.color)
        Next
    End Sub


    Private Sub client_state(list As System.Collections.Generic.List(Of Client.PlayerPackage), ballPos As System.Windows.Point, ballSpeed As System.Windows.Vector, score() As Integer) Handles client.state
        board.scores = score
        board.ball.position = ballPos
        board.ball.speed = ballSpeed
        For Each package In list
            clientBoard.players(package.gid).position = package.pos
            clientBoard.players(package.gid).speed = package.speed
        Next
    End Sub









    Private Sub bSettings_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles bSettings.Click
        If Not flyout.Opacity = 0.3 Then
            flyout.IsOpen = False
            flyout.Opacity = 0.9
        Else
            flyout.IsOpen = True
            flyout.Opacity = 1
        End If
    End Sub
    Private Sub bSettings_MouseEnter(sender As Object, e As System.Windows.Input.MouseEventArgs) Handles bSettings.MouseEnter
        If Not flyout.IsOpen Then
            If Not flyout.Opacity = 0.9 Then
                flyout.IsOpen = True
            End If
            flyout.Opacity = 0.3
        End If
    End Sub
    Private Sub bSettings_MouseLeave(sender As Object, e As System.Windows.Input.MouseEventArgs) Handles bSettings.MouseLeave
        If flyout.Opacity = 0.3 Then
            flyout.IsOpen = False
        End If
    End Sub

    Private Sub colorPlayer0_SelectionChanged(sender As Object, e As System.Windows.Controls.SelectionChangedEventArgs) Handles colorPlayer0.SelectionChanged, colorPlayer1.SelectionChanged, colorPlayer2.SelectionChanged, colorPlayer3.SelectionChanged
        Dim box As Controls.ComboBox = CType(sender, Controls.ComboBox)
        Select Case box.SelectedIndex
            Case 0
                box.Background = Brushes.Red
                box.Foreground = Brushes.Black
            Case 1
                box.Background = Brushes.Blue
                box.Foreground = Brushes.White
        End Select
    End Sub
    Private Sub checkPlayer0_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles checkPlayer0.Checked, checkPlayer1.Checked, checkPlayer2.Checked, checkPlayer3.Checked, checkPlayer0.Unchecked, checkPlayer1.Unchecked, checkPlayer2.Unchecked, checkPlayer3.Unchecked
        Dim box As Controls.CheckBox = CType(sender, Controls.CheckBox)

        Dim number As String = box.Name.Substring(box.Name.Length - 1)
        If colorPlayer0 IsNot Nothing Then
            Select Case number
                Case 0
                    colorPlayer0.IsEnabled = box.IsChecked
                Case 1
                    colorPlayer1.IsEnabled = box.IsChecked
                Case 2
                    colorPlayer2.IsEnabled = box.IsChecked
                Case 3
                    colorPlayer3.IsEnabled = box.IsChecked
            End Select
        End If

    End Sub

    Private Sub bMode_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles bMode.Click
        serverMode = Not serverMode
    End Sub

    Private Sub bServer_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles bServer.Click
        serverForm.Show()
    End Sub
End Class
