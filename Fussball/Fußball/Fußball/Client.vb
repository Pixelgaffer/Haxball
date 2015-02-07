Imports Server
Imports System.Windows.Forms


Public Class Client

    Public WithEvents client As Server.Client

    Event connectionFailed()
    Event notConnected()
    Event start(size As Double)
    Event playerGIDList(list As List(Of PlayerGID))
    Event state(list As List(Of PlayerPackage), ballPos As Point, ballSpeed As Vector, score() As Integer)
    Event finish()
    Event wrongMessage(s As String)

    Structure PlayerPackage
        Public gid As Integer
        Public pos As Point
        Public speed As Vector
    End Structure

    Structure PlayerGID
        Public gid As Integer
        Public color As Char
    End Structure

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


    Sub sendColors(color() As Char)
        If client.connected Then
            Dim s As String = "colors"

            For i = 0 To 3
                If i < color.Length Then
                    s &= ";" & i & " " & color(i)
                Else
                    s &= ";" & i & " n"
                End If
            Next

            client.send(s)
        Else
            RaiseEvent notConnected()
        End If
    End Sub

    Sub sendKeyUpdate(keys(,) As Boolean)
        If client.connected Then
            Dim s As String = "keys"
            For i = 0 To 3
                s &= ";" & i & " " & to10(keys(i, 0)) & to10(keys(i, 1)) & to10(keys(i, 2)) & to10(keys(i, 3)) & to10(keys(i, 4))
            Next
            client.send(s)
        Else
            RaiseEvent notConnected()
        End If
    End Sub


    Private Sub client_messageRecieved(s As String) Handles client.messageRecieved

        Try

            Dim firstLine As String = s.Substring(0, s.IndexOf(";"))
            Dim rest As String = s.Substring(s.IndexOf(";") + 1)


            Select Case firstLine
                Case "start"  ''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
                    Dim size As Integer = rest.Substring(5, rest.Length - 5)
                    RaiseEvent start(CDbl(size))
                Case "gids"  ''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
                    Dim list As New List(Of PlayerGID)
                    While rest.Length > 2
                        Dim gid As New PlayerGID()
                        gid.gid = CInt(rest.Substring(0, rest.IndexOf(" ")))
                        gid.color = rest.Substring(rest.IndexOf(" ") + 1, 1)
                        rest = rest.Substring(rest.IndexOf(";") + 1)
                    End While

                    RaiseEvent playerGIDList(list)

                Case "state"  ''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
                    Dim list As New List(Of PlayerPackage)
                    Dim ballPos As New Point()
                    Dim ballSpeed As New Vector()
                    Dim scores(1) As Integer

                    While rest.Length > 2
                        Dim package As New PlayerPackage()
                        package.gid = CInt(rest.Substring(0, rest.IndexOf(" ")))
                        Dim x1 As Double = CDbl(rest.Substring(rest.IndexOf("(") + 1, rest.IndexOf(":") - rest.IndexOf("(") - 1))
                        Dim y1 As Double = CDbl(rest.Substring(rest.IndexOf(":") + 1, rest.IndexOf(")") - rest.IndexOf(":") - 1))
                        rest = rest.Substring(rest.IndexOf(")") + 1)
                        Dim x2 As Double = CDbl(rest.Substring(rest.IndexOf("(") + 1, rest.IndexOf(":") - rest.IndexOf("(") - 1))
                        Dim y2 As Double = CDbl(rest.Substring(rest.IndexOf(":") + 1, rest.IndexOf(")") - rest.IndexOf(":") - 1))
                        package.pos = New Point(x1, y1)
                        package.speed = New Vector(x2, y2)
                        rest = rest.Substring(rest.IndexOf(";") + 1)

                        If package.gid = 100 Then
                            ballPos = package.pos
                            ballSpeed = package.speed
                            Exit While
                        Else
                            list.Add(package)
                        End If

                    End While

                    scores(0) = CDbl(rest.Substring(0, rest.IndexOf(":")))
                    scores(1) = CDbl(rest.Substring(rest.IndexOf(":") + 1))

                    RaiseEvent state(list, ballPos, ballSpeed, scores)

                Case "end"  ''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
                    RaiseEvent finish()
                Case Else
                    RaiseEvent wrongMessage(s)
            End Select

        Catch ex As ArgumentOutOfRangeException
            RaiseEvent wrongMessage(s)
        End Try

    End Sub







    Private Sub client_connectionFailed() Handles client.connectionFailed
        RaiseEvent connectionFailed()
    End Sub

    Function to10(bool As Boolean)
        If bool Then
            Return 1
        Else
            Return 0
        End If
    End Function

End Class
