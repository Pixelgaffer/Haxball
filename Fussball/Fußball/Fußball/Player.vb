Imports System.Windows.Forms

Public Class Player
    Inherits Entity


    Property team As Boolean = False
    Property keyUp As Keys = Keys.W
    Property keyDown As Keys = Keys.S
    Property keyLeft As Keys = Keys.A
    Property keyRight As Keys = Keys.D
    Property keyShoot As Keys = Keys.Space
    Public doesShoot As Boolean = False
    Public shootDistance As Double = 35
    Public shootForce As Double = 10
    Public maxSpeed As Double = 5
    Public moveKeyDown(3) As Boolean


    Private Declare Function GetAsyncKeyState Lib "user32" Alias "GetAsyncKeyState" (ByVal vKey As Keys) As Int16
    Private Function IsKeyPressed(ByVal vKey As Keys) As Boolean
        Return (GetAsyncKeyState(vKey) And &H8000) = &H8000
    End Function

    Sub New(pos As Point, t As Boolean)
        MyBase.New(pos, New Vector(0, 0))
        team = t
        setColor()
    End Sub

    Sub New(boardSize As Integer, t As Boolean, rand As Random)
        MyBase.New()
        team = t
        setColor()

        randomPos(boardSize, rand)

    End Sub

    Sub randomPos(boardSize As Integer, rand As Random)  '''''''''''''''''''komischer Bug
        position.Y = rand.NextDouble() * boardSize / 2
        position.X = rand.NextDouble() * boardSize / 2
        If (team) Then
            position.X += boardSize / 2  'nach rechts verschieben, wenn es die rechte Mannschaft ist
        End If
    End Sub


    Private Sub setColor()
        If (team) Then
            color = Colors.Red
        Else
            color = Colors.Blue
        End If
    End Sub

    Sub checkMove()
        If isKeyPressed(keyUp) Then
            speed.Y = -maxSpeed
        End If
        If isKeyPressed(keyDown) Then
            speed.Y = maxSpeed
        End If
        If isKeyPressed(keyLeft) Then
            speed.X = -maxSpeed
        End If
        If isKeyPressed(keyRight) Then
            speed.X = maxSpeed
        End If

        If isKeyPressed(keyShoot) Then
            If Not doesShoot Then
                color.A = 150
                doesShoot = True
            End If
        Else
            If doesShoot Then
                color.A = 255
                doesShoot = False
            End If
        End If

    End Sub



    Overrides Function draw() As List(Of UIElement)
        Return MyBase.draw()
    End Function


End Class
