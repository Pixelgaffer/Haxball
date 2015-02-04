Imports Server
Imports System.Windows.Forms


Public Class Client

    Public WithEvents client As Server.Client

    Event connectionFailed()
    Event notConnected()


    Private Declare Function GetAsyncKeyState Lib "user32" Alias "GetAsyncKeyState" (ByVal vKey As Keys) As Int16
    Private Function IsKeyPressed(ByVal vKey As Keys) As Boolean
        Return (GetAsyncKeyState(vKey) And &H8000) = &H8000
    End Function


    Sub New(i As String, p As Integer)
        client = New Server.Client(i, p, "hallo")
    End Sub


    Sub connect()
        If client.connect() = False Then
            RaiseEvent connectionFailed()
        End If
    End Sub


    Sub sendPlayers(color() As Char)

        For i = 0 To 3
            If i < color.Length Then

            Else

            End If
        Next
    End Sub

    Sub sendKeyUpdate(keys(,) As Boolean)
        If client.connected Then
            Dim s As String = ""
            For i = 0 To 3
                s &= i & " " & to10(keys(i, 0)) & to10(keys(i, 1)) & to10(keys(i, 2)) & to10(keys(i, 3))
                If Not i = 3 Then
                    s &= vbLf
                End If
            Next
            client.send(s)
        Else
            RaiseEvent notConnected()
        End If
    End Sub



    Function to10(bool As Boolean)
        If bool Then
            Return 1
        Else
            Return 0
        End If
    End Function

    Private Sub client_connectionFailed() Handles client.connectionFailed
        RaiseEvent connectionFailed()
    End Sub
End Class
