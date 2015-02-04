
Public Class ServerForm

    Public WithEvents server As MyServer




    Private Sub bServer_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles bServer.Click
        server.start()
    End Sub

    Private Sub bGame_Click(sender As Object, e As System.Windows.RoutedEventArgs) Handles bGame.Click

    End Sub


End Class
