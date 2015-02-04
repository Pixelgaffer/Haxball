Imports System.Windows.Forms

Class MainWindow

    Public board As New Board()
    Public client As New Client("localhost", 8001)
    Private WithEvents timer As New Forms.Timer()

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



    Sub draw()
        If cViewer IsNot Nothing Then

            cViewer.Children.Clear()
            For Each element In board.draw()
                If TypeOf (element) Is Shape Then
                    CType(element, Shape).Stroke = Brushes.Black
                End If

                cViewer.Children.Add(element)
            Next

        End If
    End Sub





    Private Sub timer_Tick(sender As Object, e As System.EventArgs) Handles Timer.Tick
        draw()

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



End Class
