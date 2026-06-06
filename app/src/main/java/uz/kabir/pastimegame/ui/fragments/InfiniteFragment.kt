package uz.kabir.pastimegame.ui.fragments

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import uz.kabir.pastimegame.ui.views.AnimationButton.animateClick
import uz.kabir.pastimegame.R
import uz.kabir.pastimegame.data.local.SoundSharedPreference
import uz.kabir.pastimegame.databinding.FragmentBlotBinding
import kotlin.random.Random

class InfiniteFragment : Fragment() {

    private var _binding: FragmentBlotBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttons: Array<Array<ImageButton>>

    private var player1Moves = mutableListOf<Pair<Int, Int>>()
    private var player2Moves = mutableListOf<Pair<Int, Int>>()
    private var currentPlayer = 1
    private var player1Score = 0
    private var player2Score = 0

    private var user1ImageIndex: Int = 0
    private var user2ImageIndex: Int = 0
    private var user1Name: String? = null
    private var user2Name: String? = null

    private val userImages = arrayOf(
        R.drawable.ic_character_woman_1,
        R.drawable.ic_character_woman_5,
        R.drawable.ic_character_woman_4,
        R.drawable.ic_character_man_3,
        R.drawable.ic_character_man_2,
        R.drawable.ic_character_man_4,
    )

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var equalizer: Equalizer
    private var audioOn = false

    private var restoredStatusText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlotBinding.inflate(inflater, container, false)
        audioOn = SoundSharedPreference.getStateAudio(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initGame()

        if (savedInstanceState == null) {
            resetBoard()
        }

        savedInstanceState?.let {
            currentPlayer = it.getInt("currentPlayer")
            player1Score = it.getInt("player1Score")
            player2Score = it.getInt("player2Score")

            restoredStatusText = it.getString("statusText", "")

            player1Moves.clear()
            player2Moves.clear()

            it.getString("player1Moves", "")
                .split(";")
                .filter { item -> item.isNotEmpty() }
                .forEach { item ->
                    val (r, c) = item.split(",")
                    player1Moves.add(
                        Pair(
                            r.toInt(),
                            c.toInt()
                        )
                    )
                }

            it.getString("player2Moves", "")
                .split(";")
                .filter { item -> item.isNotEmpty() }
                .forEach { item ->

                    val (r, c) = item.split(",")
                    player2Moves.add(
                        Pair(
                            r.toInt(),
                            c.toInt()
                        )
                    )
                }

            restoreGameUI()
        }

        arguments?.let {
            user1ImageIndex = it.getInt("user1ImageIndex")
            user2ImageIndex = it.getInt("user2ImageIndex")
            user1Name = it.getString("user1Name")
            user2Name = it.getString("user2Name")
        }
        binding.txtUserNameX.text = user1Name
        binding.txtUserNameO.text = user2Name
        binding.imgUserX.setBackgroundResource(userImages[user1ImageIndex])
        binding.imgUserO.setBackgroundResource(userImages[user2ImageIndex])
    }

    private fun restoreGameUI() {
        updateScore()

        binding.statusText.text = restoredStatusText

        player1Moves.forEachIndexed { index, move ->
            buttons[move.first][move.second].setImageResource(
                if (index == 0 && player1Moves.size == 3)
                    R.drawable.faded_o
                else
                    R.drawable.ic_main_o
            )
        }

        player2Moves.forEachIndexed { index, move ->
            buttons[move.first][move.second].setImageResource(
                if (index == 0 && player2Moves.size == 3)
                    R.drawable.faded_x
                else
                    R.drawable.ic_main_x
            )
        }

        if (currentPlayer == 1) {
            binding.animationView1.visibility = View.VISIBLE
            binding.animationView2.visibility = View.INVISIBLE
        } else {
            binding.animationView2.visibility = View.VISIBLE
            binding.animationView1.visibility = View.INVISIBLE
        }
    }

    private fun initViews() {
        buttons = arrayOf(
            arrayOf(
                binding.button00, binding.button01, binding.button02
            ),
            arrayOf(
                binding.button10, binding.button11, binding.button12
            ),
            arrayOf(
                binding.button20, binding.button21, binding.button22
            )
        )
    }

    private fun initGame() {
        binding.resetButton.setOnClickListener {
            resetBoard()
            binding.resetButton.animateClick()
        }
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                buttons[i][j].setOnClickListener {
                    onButtonClick(i, j)
                    buttons[i][j].animateClick(40)
                }
            }
        }
    }

    private fun resetBoard() {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                buttons[i][j].setImageResource(0)
                buttons[i][j].isEnabled = true
                buttons[i][j].backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color.defaultBackground)
            }
        }
        currentPlayer = Random.nextInt(1, 3)

        player1Moves.clear()
        player2Moves.clear()
        updateScore()
        binding.statusText.text = ""
        binding.indicatorUser1.setImageResource(R.drawable.ic_main_o)
        binding.indicatorUser2.setImageResource(R.drawable.ic_main_x)
        binding.animationView1.visibility = View.INVISIBLE
        binding.animationView2.visibility = View.INVISIBLE

        if (currentPlayer == 1) {
            binding.animationView1.visibility = View.VISIBLE
            binding.animationView2.visibility = View.INVISIBLE
        } else {
            binding.animationView2.visibility = View.VISIBLE
            binding.animationView1.visibility = View.INVISIBLE
        }
    }

    private fun onButtonClick(row: Int, col: Int) {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                buttons[i][j].backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.defaultBackground)
            }
        }

        val button = buttons[row][col]
        if (button.drawable == null) {
            if (currentPlayer == 1) {

                if (audioOn)
                setAudioFile(true)

                button.setImageResource(R.drawable.ic_main_o)
                player1Moves.add(Pair(row, col))
                if (player1Moves.size > 3) {
                    val removedMove = player1Moves.removeAt(0)
                    buttons[removedMove.first][removedMove.second].setImageResource(0)
                    fadeButton(player2Moves.first()) // Fade the oldest move
                } else if (player2Moves.size == 3) {
                    fadeButton(player2Moves.first())
                }

                val winPositions = checkWin(player1Moves)
                if (winPositions != null) {
                    player1Score++
                    binding.statusText.text = resources.getString(R.string.o_player_won)
                    binding.indicatorUser1.setImageResource(R.drawable.ic_winner)
                    disableButtons()
                    updateScore()
                    highlightWin(winPositions)
                    binding.animationView1.visibility = View.VISIBLE
                } else if (isBoardFull()) {
                    binding.statusText.text = getString(R.string.draw)
                    disableButtons()
                } else {
                    currentPlayer = 2
                    binding.animationView2.visibility = View.VISIBLE
                    binding.animationView1.visibility = View.INVISIBLE
                }
            } else {

                if (audioOn)
                setAudioFile(false)


                button.setImageResource(R.drawable.ic_main_x)
                player2Moves.add(Pair(row, col))
                if (player2Moves.size > 3) {
                    val removedMove = player2Moves.removeAt(0)
                    buttons[removedMove.first][removedMove.second].setImageResource(0)
                    fadeButton(player1Moves.first()) // Fade the oldest move
                } else if (player1Moves.size == 3) {
                    fadeButton(player1Moves.first())
                }

                val winPositions = checkWin(player2Moves)
                if (winPositions != null) {
                    player2Score++
                    binding.statusText.text = resources.getString(R.string.x_player_won)
                    binding.indicatorUser2.setImageResource(R.drawable.ic_winner)
                    disableButtons()
                    updateScore()
                    highlightWin(winPositions)
                    binding.animationView2.visibility = View.VISIBLE
                } else if (isBoardFull()) {
                    binding.statusText.text = getString(R.string.draw)
                    disableButtons()
                } else {
                    currentPlayer = 1
                    binding.animationView1.visibility = View.VISIBLE
                    binding.animationView2.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun checkWin(moves: List<Pair<Int, Int>>): List<Pair<Int, Int>>? {
        if (moves.size < 3) return null
        // Check rows
        for (i in 0 until 3) {
            val rowMoves = moves.filter { it.first == i }
            if (rowMoves.size >= 3) return rowMoves
        }
        // Check columns
        for (i in 0 until 3) {
            val colMoves = moves.filter { it.second == i }
            if (colMoves.size >= 3) return colMoves
        }
        // Check diagonals
        val diag1 = listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
        val diag2 = listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))

        if (moves.containsAll(diag1)) return diag1
        if (moves.containsAll(diag2)) return diag2

        return null
    }

    private fun highlightWin(winPositions: List<Pair<Int, Int>>) {
        for (pos in winPositions) {
            buttons[pos.first][pos.second].backgroundTintList =
                ContextCompat.getColorStateList(binding.root.context, R.color.winBackground)
        }
    }

    private fun isBoardFull(): Boolean {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (buttons[i][j].drawable == null) return false
            }
        }
        return true
    }

    private fun disableButtons() {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                buttons[i][j].isEnabled = false
            }
        }
    }

    private fun fadeButton(move: Pair<Int, Int>) {
        val button = buttons[move.first][move.second]

        if (player1Moves.contains(move)) {
            button.setImageResource(R.drawable.faded_o)
        } else if (player2Moves.contains(move)) {
            button.setImageResource(R.drawable.faded_x)
        }
    }

    private fun updateScore() {
        binding.scoreTablePlayer1.text = "$player1Score"
        binding.scoreTablePlayer2.text = "$player2Score"

    }

    private fun setAudioFile(boolean: Boolean) {
        if (boolean) {
            mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.audio_1)
            mediaPlayer.setVolume(0.3f, 0.3f)
        } else {
            mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.audio_2)
            mediaPlayer.setVolume(0.3f, 0.3f)
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.start()


        mediaPlayer.setOnPreparedListener {
            try {
                equalizer = Equalizer(0, mediaPlayer.audioSessionId).apply {
                    enabled = true
                    for (i in 0 until numberOfBands) {
                        setBandLevel(
                            i.toShort(),
                            1000.toShort()
                        )
                    }
                }
                Log.d("Equalizer", "Equalizer successfully configured")
            } catch (e: Exception) {
                Log.e("Equalizer", "Failed to initialize Equalizer: ${e.message}")
            }
        }
        mediaPlayer.setOnCompletionListener {
            it.release()
            Log.d("MediaPlayer", "MediaPlayer released")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPlayer", currentPlayer)
        outState.putInt("player1Score", player1Score)
        outState.putInt("player2Score", player2Score)
        outState.putString("statusText", binding.statusText.text.toString())
        outState.putString("player1Moves", player1Moves.joinToString(";") { "${it.first},${it.second}" })
        outState.putString("player2Moves", player2Moves.joinToString(";") { "${it.first},${it.second}" })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}