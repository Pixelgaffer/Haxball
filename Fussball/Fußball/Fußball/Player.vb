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
    Public isVisible As Boolean = True

    Public moveKeyDown(4) As Boolean
    Public usesKeys As Boolean = True


    Private Declare Function GetAsyncKeyState Lib "user32" Alias "GetAsyncKeyState" (ByVal vKey As Keys) As Int16
    Private Function IsKeyPressed(ByVal vKey As Keys) As Boolean
        Return (GetAsyncKeyState(vKey) And &H8000) = &H8000
    End Function

    Sub New(visible As Boolean)
        MyBase.New()
        isVisible = visible
    End Sub

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
        If usesKeys Then
            If IsKeyPressed(keyUp) Then
                speed.Y = -maxSpeed
            End If
            If IsKeyPressed(keyDown) Then
                speed.Y = maxSpeed
            End If
            If IsKeyPressed(keyLeft) Then
                speed.X = -maxSpeed
            End If
            If IsKeyPressed(keyRight) Then
                speed.X = maxSpeed
            End If

            If IsKeyPressed(keyShoot) Then
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



        Else

            If moveKeyDown(0) Then  'W
                speed.Y = -maxSpeed
            End If
            If moveKeyDown(1) Then  'A
                speed.X = -maxSpeed
            End If
            If moveKeyDown(2) Then  'S
                speed.Y = maxSpeed
            End If
            If moveKeyDown(3) Then  'D
                speed.X = maxSpeed
            End If
            If moveKeyDown(4) Then  'Schuss
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
        End If

    End Sub



    Overrides Function draw() As List(Of UIElement)
        If isVisible Then
            Return MyBase.draw()
        Else
            Return New List(Of UIElement)
        End If
    End Function


End Class
